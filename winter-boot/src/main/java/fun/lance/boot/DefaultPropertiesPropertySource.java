package fun.lance.boot;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

public class DefaultPropertiesPropertySource extends MapPropertySource {

    public static final String NAME = "defaultProperties";

    public DefaultPropertiesPropertySource(Map<String, Object> source) {
        super(NAME, source);
    }

    public static void addOrMerge(Map<String, Object> source, MutablePropertySources sources) {
        if (!CollectionUtils.isEmpty(source)) {
            Map<String, Object> resultingSource = new HashMap<>();
            DefaultPropertiesPropertySource propertySource = new DefaultPropertiesPropertySource(resultingSource);
            if (sources.contains(NAME)) {
                mergeIfPossible(source, sources, resultingSource);
                sources.replace(NAME, propertySource);
            }
            else {
                resultingSource.putAll(source);
                sources.addLast(propertySource);
            }
        }
    }

    public static void moveToEnd(ConfigurableEnvironment environment) {
        moveToEnd(environment.getPropertySources());
    }

    public static void moveToEnd(MutablePropertySources propertySources) {
        PropertySource<?> propertySource = propertySources.remove(NAME);
        if (propertySource != null) {
            propertySources.addLast(propertySource);
        }
    }

    private static void mergeIfPossible(Map<String, Object> source, MutablePropertySources sources,
                                        Map<String, Object> resultingSource) {
        PropertySource<?> existingSource = sources.get(NAME);
        if (existingSource != null) {
            Object underlyingSource = existingSource.getSource();
            if (underlyingSource instanceof Map) {
                resultingSource.putAll((Map<String, Object>) underlyingSource);
            }
            resultingSource.putAll(source);
        }
    }
}
