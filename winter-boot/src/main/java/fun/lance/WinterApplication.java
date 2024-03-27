package fun.lance;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

import java.util.*;

public class WinterApplication {

    private final Set<Class<?>> primarySources;

    private ResourceLoader resourceLoader;
    private WebApplicationType webApplicationType;

    public WinterApplication(Class<?>... primarySources) {
        this(null, primarySources);
    }

    public WinterApplication(ResourceLoader resourceLoader, Class<?>... primarySources) {
        this.resourceLoader = resourceLoader;
        Assert.notNull(primarySources, "主程序不能为空");
        this.primarySources = new LinkedHashSet<>(Arrays.asList(primarySources));
        this.webApplicationType = WebApplicationType.deduceFromClasspath();
    }

    public ConfigurableApplicationContext run(String... args) {
        return null;
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
}
