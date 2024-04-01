package fun.lance.boot.context.properties.source;

import org.springframework.core.env.PropertySource;

class ConfigurationPropertySourcesPropertySource extends PropertySource<Iterable<ConfigurationPropertySource>> {

    ConfigurationPropertySourcesPropertySource(String name, Iterable<ConfigurationPropertySource> source) {
        super(name, source);
    }

    @Override
    public Object getProperty(String name) {
        return null;
    }
}
