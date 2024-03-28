package fun.lance.boot;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.util.*;
import java.lang.StackWalker.StackFrame;
import java.util.stream.Stream;

public class WinterApplication {

    static final WinterApplicationShutdownHook shutdownHook = new WinterApplicationShutdownHook();

    private static final String SYSTEM_PROPERTY_JAVA_AWT_HEADLESS = "java.awt.headless";
    private static final Log logger = LogFactory.getLog(WinterApplication.class);
    private static final ThreadLocal<WinterApplicationHook> applicationHook = new ThreadLocal<>();

    private final Set<Class<?>> primarySources;
    private final List<BootstrapRegistryInitializer> bootstrapRegistryInitializers;

    private boolean registerShutdownHook = true;
    private boolean headless = true;

    private Class<?> mainApplicationClass;
    private ResourceLoader resourceLoader;
    private WebApplicationType webApplicationType;
    private ApplicationStartup applicationStartup = ApplicationStartup.DEFAULT;

    private List<ApplicationContextInitializer<?>> initializers;
    private List<ApplicationListener<?>> listeners;

    public WinterApplication(Class<?>... primarySources) {
        this(null, primarySources);
    }

    public WinterApplication(ResourceLoader resourceLoader, Class<?>... primarySources) {
        this.resourceLoader = resourceLoader;
        Assert.notNull(primarySources, "主程序不能为空");
        this.primarySources = new LinkedHashSet<>(Arrays.asList(primarySources));
        this.webApplicationType = WebApplicationType.deduceFromClasspath();
        this.bootstrapRegistryInitializers = new ArrayList<>(getSpringFactoriesInstances(BootstrapRegistryInitializer.class));
        setInitializers((Collection) getSpringFactoriesInstances(ApplicationContextInitializer.class));
        setListeners((Collection) getSpringFactoriesInstances(ApplicationListener.class));
        this.mainApplicationClass = deduceMainApplicationClass();
    }

    /**
     * 核心方法
     */
    public ConfigurableApplicationContext run(String... args) {
        if (this.registerShutdownHook) {
            WinterApplication.shutdownHook.enableShutdownHookAddition();
        }
        long startTime = System.nanoTime();
        DefaultBootstrapContext bootstrapContext = createBootstrapContext();

        ConfigurableApplicationContext context = null;
        configureHeadlessProperty();
        WinterApplicationRunListeners runListeners = getRunListeners(args);

        return context;
    }

    /**
     * 外部程序调用的主入口
     */
    public static ConfigurableApplicationContext run(Class<?> primarySource, String... args) {
        return run(new Class[]{primarySource}, args);
    }

    public static ConfigurableApplicationContext run(Class<?>[] primarySources, String[] args) {
        return new WinterApplication(primarySources).run(args);
    }

    public ClassLoader getClassLoader() {
        if (this.resourceLoader != null) {
            return this.resourceLoader.getClassLoader();
        }
        return ClassUtils.getDefaultClassLoader();
    }

    public void setInitializers(Collection<? extends ApplicationContextInitializer<?>> initializers) {
        this.initializers = new ArrayList<>(initializers);
    }

    public void setListeners(Collection<? extends ApplicationListener<?>> listeners) {
        this.listeners = new ArrayList<>(listeners);
    }

    private Class<?> deduceMainApplicationClass() {
        return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .walk(this::findMainClass)
                .orElse(null);
    }

    private Optional<Class<?>> findMainClass(Stream<StackFrame> stack) {
        return stack.filter((frame) -> Objects.equals(frame.getMethodName(), "main"))
                .findFirst()
                .map(StackFrame::getDeclaringClass);

    }

    private DefaultBootstrapContext createBootstrapContext() {
        DefaultBootstrapContext bootstrapContext = new DefaultBootstrapContext();
        this.bootstrapRegistryInitializers.forEach((initializer) -> initializer.initialize(bootstrapContext));
        return bootstrapContext;
    }

    /**
     * 设置java.awt.headless属性，默认为true
     * 用于指示Java虚拟机是否应该在没有显示器、键盘和鼠标的环境中运行
     */
    private void configureHeadlessProperty() {
        System.setProperty(
                SYSTEM_PROPERTY_JAVA_AWT_HEADLESS,
                System.getProperty(SYSTEM_PROPERTY_JAVA_AWT_HEADLESS, Boolean.toString(this.headless))
        );
    }

    /**
     * 获取程序RunListener
     * 一般情况下是context.event.EventPublishingRunListener
     */
    private WinterApplicationRunListeners getRunListeners(String[] args) {
        SpringFactoriesLoader.ArgumentResolver  argumentResolver = SpringFactoriesLoader.ArgumentResolver.of(WinterApplication.class, this);
        argumentResolver = argumentResolver.and(String[].class, args);
        List<WinterApplicationRunListener> runListeners = getSpringFactoriesInstances(WinterApplicationRunListener.class, argumentResolver);
        WinterApplicationHook hook = applicationHook.get();
        WinterApplicationRunListener hookListener = (hook != null) ? hook.getRunListener(this) : null;
        if (hookListener != null) {
            runListeners = new ArrayList<>(runListeners);
            runListeners.add(hookListener);
        }
        return new WinterApplicationRunListeners(logger, runListeners, this.applicationStartup);
    }

    private <T> List<T> getSpringFactoriesInstances(Class<T> type) {
        return getSpringFactoriesInstances(type, null);
    }

    private <T> List<T> getSpringFactoriesInstances(Class<T> type, SpringFactoriesLoader.ArgumentResolver argumentResolver) {
        return SpringFactoriesLoader.forDefaultResourceLocation(getClassLoader()).load(type, argumentResolver);
    }


}
