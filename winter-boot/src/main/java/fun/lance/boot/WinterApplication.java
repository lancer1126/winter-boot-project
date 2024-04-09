package fun.lance.boot;

import fun.lance.boot.context.properties.bind.Binder;
import fun.lance.boot.context.properties.source.ConfigurationPropertySources;
import fun.lance.boot.convert.ApplicationConversionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.env.*;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.lang.StackWalker.StackFrame;
import java.util.stream.Stream;

public class WinterApplication {

    static final WinterApplicationShutdownHook shutdownHook = new WinterApplicationShutdownHook();

    private static final String SYSTEM_PROPERTY_JAVA_AWT_HEADLESS = "java.awt.headless";
    private static final ThreadLocal<WinterApplicationHook> applicationHook = new ThreadLocal<>();
    private static final Log logger = LogFactory.getLog(WinterApplication.class);

    private final Set<Class<?>> primarySources;
    private final List<BootstrapRegistryInitializer> bootstrapRegistryInitializers;

    private boolean headless = true;
    private boolean registerShutdownHook = true;
    private boolean addConversionService = true;
    private boolean addCommandLineProperties = true;

    private Class<?> mainApplicationClass;
    private WebApplicationType webApplicationType;
    private ResourceLoader resourceLoader;
    private ConfigurableEnvironment environment;
    private ApplicationStartup applicationStartup = ApplicationStartup.DEFAULT;
    private ApplicationContextFactory applicationContextFactory = ApplicationContextFactory.DEFAULT;

    private List<ApplicationContextInitializer<?>> initializers;
    private List<ApplicationListener<?>> listeners;
    private Map<String, Object> defaultProperties;

    public WinterApplication(Class<?>... primarySources) {
        this(null, primarySources);
    }

    /**
     * 核心构造方法
     */
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
     * 外部程序调用的主入口
     */
    public static ConfigurableApplicationContext run(Class<?> primarySource, String... args) {
        return run(new Class[]{primarySource}, args);
    }

    /**
     * 初始化一个WinterApplication实例后调用实例的run方法
     */
    public static ConfigurableApplicationContext run(Class<?>[] primarySources, String[] args) {
        return new WinterApplication(primarySources).run(args);
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
        runListeners.starting(bootstrapContext, this.mainApplicationClass);

        try {
            ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);
            ConfigurableEnvironment environment = prepareEnvironment(runListeners, bootstrapContext, applicationArguments);
        } catch (Throwable ex) {

        }

        return context;
    }

    /**
     * 获取ClassLoader
     * 初始化为Thread.currentThread().getContextClassLoader();
     */
    public ClassLoader getClassLoader() {
        if (this.resourceLoader != null) {
            return this.resourceLoader.getClassLoader();
        }
        return ClassUtils.getDefaultClassLoader();
    }

    /**
     * 从类路径中的spring.factories读取ApplicationContextInitializer的配置
     */
    public void setInitializers(Collection<? extends ApplicationContextInitializer<?>> initializers) {
        this.initializers = new ArrayList<>(initializers);
    }

    /**
     * 从类路径中的spring.factories读取ApplicationListener的配置
     */
    public void setListeners(Collection<? extends ApplicationListener<?>> listeners) {
        this.listeners = new ArrayList<>(listeners);
    }

    /**
     * 获取已初始化的listener
     */
    public Set<ApplicationListener<?>> getListeners() {
        return asUnmodifiableOrderedSet(this.listeners);
    }

    /**
     * 配置ConfigurableEnvironment
     * 内容有配置一些转换器
     */
    protected void configureEnvironment(ConfigurableEnvironment environment, String[] args) {
        if (this.addConversionService) {
            // 配置转换器，包括日期与字符的转换等
            environment.setConversionService(new ApplicationConversionService());
        }
        configurePropertySources(environment, args);
        configureProfiles(environment, args);
    }

    /**
     * 从args中读取配置项
     * 默认启动条件下两个if条件都不会执行
     */
    protected void configurePropertySources(ConfigurableEnvironment environment, String[] args) {
        MutablePropertySources sources = environment.getPropertySources();
        if (!CollectionUtils.isEmpty(this.defaultProperties)) {
            DefaultPropertiesPropertySource.addOrMerge(this.defaultProperties, sources);
        }
        if (this.addCommandLineProperties && args.length > 0) {
            String name = CommandLinePropertySource.COMMAND_LINE_PROPERTY_SOURCE_NAME;
            if (sources.contains(name)) {
                PropertySource<?> source = sources.get(name);
                CompositePropertySource composite = new CompositePropertySource(name);
                composite.addPropertySource(new SimpleCommandLinePropertySource("springApplicationCommandLineArgs", args));
                composite.addPropertySource(source);
                sources.replace(name, composite);
            }
        }
    }

    /**
     * 不知为何是空方法，源码中注释为
     * Configure which profiles are active (or active by default) for this application
     * environment. Additional profiles may be activated during configuration file
     * processing through the {@code spring.profiles.active} property.
     */
    protected void configureProfiles(ConfigurableEnvironment environment, String[] args) {
    }

    /**
     * 将environment绑定到WinterApplication中
     */
    protected void bindToWinterApplication(ConfigurableEnvironment environment) {
        try {

        } catch (Exception ex) {
            throw new IllegalStateException("Cannot bind to WinterApplication", ex);
        }
    }

    /**
     * 将原集合转换为一个不可修改的集合
     */
    private static <E> Set<E> asUnmodifiableOrderedSet(Collection<E> elements) {
        List<E> list = new ArrayList<>(elements);
        list.sort(AnnotationAwareOrderComparator.INSTANCE);
        return new LinkedHashSet<>(list);
    }

    /**
     * 推断主程序
     */
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

    /**
     * 创建 ConfigurableBootstrapContext 实现类为 DefaultBootstrapContext
     */
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
        SpringFactoriesLoader.ArgumentResolver argumentResolver = SpringFactoriesLoader.ArgumentResolver.of(WinterApplication.class, this);
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

    /**
     * 创建ConfigurableEnvironment并为其赋上一些属性
     */
    private ConfigurableEnvironment prepareEnvironment(WinterApplicationRunListeners listeners,
                                                       DefaultBootstrapContext bootstrapContext,
                                                       ApplicationArguments applicationArguments) {
        // 创建 ConfigurableEnvironment (ApplicationServletEnvironment)
        ConfigurableEnvironment environment = getOrCreateEnvironment();
        configureEnvironment(environment, applicationArguments.getSourceArgs());
        ConfigurationPropertySources.attach(environment);
        listeners.environmentPrepared(bootstrapContext, environment);
        DefaultPropertiesPropertySource.moveToEnd(environment);
        Assert.state(!environment.containsProperty("spring.main.environment-prefix"),
                "Environment prefix cannot be set via properties.");

        // todo
        return null;
    }

    /**
     * 创建一个ConfigurableEnvironment
     * 根据webApplicationType来判断，一般为SERVLET
     * 所以实现类为 ApplicationServletEnvironment
     */
    private ConfigurableEnvironment getOrCreateEnvironment() {
        if (this.environment != null) {
            return this.environment;
        }
        ConfigurableEnvironment environment = this.applicationContextFactory.createEnvironment(this.webApplicationType);
        if (environment == null && this.applicationContextFactory != ApplicationContextFactory.DEFAULT) {
            environment = ApplicationContextFactory.DEFAULT.createEnvironment(this.webApplicationType);
        }
        return (environment != null) ? environment : new ApplicationEnvironment();
    }

    /**
     * 处理程序启动失败的情况
     * 从处理失败的方式可以看出，启动过程中最重要的属性是
     * ConfigurableApplicationContext和WinterApplicationRunListeners（SpringApplicationRunListeners）
     */
    private RuntimeException handleRunFailure(ConfigurableApplicationContext context, Throwable exception,
                                              WinterApplicationRunListeners listeners) {
        if (exception instanceof AbandonedRunException abandonedRunException) {
            return abandonedRunException;
        }
        try {

        } catch (Exception ex) {
            logger.warn("Unable to close ApplicationContext", ex);
        }
        return (exception instanceof RuntimeException runtimeException) ? runtimeException : new IllegalStateException(exception);
    }

    /**
     * 从类路径下 /META-INF/spring.factories中读取映射配置
     */
    private <T> List<T> getSpringFactoriesInstances(Class<T> type) {
        return getSpringFactoriesInstances(type, null);
    }

    /**
     * 从类路径下 /META-INF/spring.factories中读取映射配置，带上参数
     */
    private <T> List<T> getSpringFactoriesInstances(Class<T> type, SpringFactoriesLoader.ArgumentResolver argumentResolver) {
        return SpringFactoriesLoader.forDefaultResourceLocation(getClassLoader()).load(type, argumentResolver);
    }

    /**
     * 抛出此异常可以安全的退出运行中的WinterApplication，不需要处理运行失败的情况
     */
    public static class AbandonedRunException extends RuntimeException {

        private final ConfigurableApplicationContext applicationContext;

        public AbandonedRunException() {
            this(null);
        }

        public AbandonedRunException(ConfigurableApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }

        public ConfigurableApplicationContext getApplicationContext() {
            return this.applicationContext;
        }
    }


}
