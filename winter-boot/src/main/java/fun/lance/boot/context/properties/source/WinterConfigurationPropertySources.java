package fun.lance.boot.context.properties.source;

import org.springframework.core.env.PropertySource;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.util.Iterator;
import java.util.Map;

public class WinterConfigurationPropertySources implements Iterable<ConfigurationPropertySource> {

    private final Iterable<PropertySource<?>> sources;
    private final Map<PropertySource<?>, ConfigurationPropertySource> cache = new ConcurrentReferenceHashMap<>(
            16, ConcurrentReferenceHashMap.ReferenceType.SOFT
    );

    WinterConfigurationPropertySources(Iterable<PropertySource<?>> sources) {
        Assert.notNull(sources, "Sources must not be null");
        this.sources = sources;
    }

    boolean isUsingSources(Iterable<PropertySource<?>> sources) {
        return this.sources == sources;
    }


    @Override
    public Iterator<ConfigurationPropertySource> iterator() {
        return null;
    }
}
