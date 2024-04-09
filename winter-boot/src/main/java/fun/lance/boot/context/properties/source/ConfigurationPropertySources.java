package fun.lance.boot.context.properties.source;

import org.springframework.core.env.*;
import org.springframework.util.Assert;

public final class ConfigurationPropertySources {

    private static final String ATTACHED_PROPERTY_SOURCE_NAME = "configurationProperties";

    public static void attach(Environment environment) {
        Assert.isInstanceOf(ConfigurableEnvironment.class, environment);
        MutablePropertySources sources = ((ConfigurableEnvironment) environment).getPropertySources();
        PropertySource<?> attached = getAttached(sources);
        if (attached == null || !isUsingSources(attached, sources)) {
            attached = new ConfigurationPropertySourcesPropertySource(
                    ATTACHED_PROPERTY_SOURCE_NAME, new WinterConfigurationPropertySources(sources)
            );
        }
        sources.remove(ATTACHED_PROPERTY_SOURCE_NAME);
        sources.addFirst(attached);
    }

    public static Iterable<ConfigurationPropertySource> get(Environment environment) {
        Assert.isInstanceOf(ConfigurableEnvironment.class, environment);
        MutablePropertySources sources = ((ConfigurableEnvironment) environment).getPropertySources();
        ConfigurationPropertySourcesPropertySource attached = (ConfigurationPropertySourcesPropertySource) sources.get(ATTACHED_PROPERTY_SOURCE_NAME);
        if (attached == null) {
            return from(sources);
        }
        return attached.getSource();
    }

    public static Iterable<ConfigurationPropertySource> from(Iterable<PropertySource<?>> sources) {
        return new WinterConfigurationPropertySources(sources);
    }

    static PropertySource<?> getAttached(MutablePropertySources sources) {
        return (sources != null) ? sources.get(ATTACHED_PROPERTY_SOURCE_NAME) : null;
    }

    private static boolean isUsingSources(PropertySource<?> attached, MutablePropertySources sources) {
        return attached instanceof ConfigurationPropertySourcesPropertySource
                && ((WinterConfigurationPropertySources) attached.getSource()).isUsingSources(sources);
    }
}
