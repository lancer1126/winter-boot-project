package fun.lance.boot.context.properties.bind;

import fun.lance.boot.context.properties.source.ConfigurationPropertySource;
import fun.lance.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import java.util.*;
import java.util.function.Consumer;

public class Binder {

    public Binder(Iterable<ConfigurationPropertySource> sources, PlaceholdersResolver placeholdersResolver,
                  ConversionService conversionService, Consumer<PropertyEditorRegistry> propertyEditorInitializer,
                  BindHandler defaultBindHandler) {
        this(sources, placeholdersResolver, conversionService, propertyEditorInitializer, defaultBindHandler, null);
    }

    public Binder(Iterable<ConfigurationPropertySource> sources, PlaceholdersResolver placeholdersResolver,
                  ConversionService conversionService, Consumer<PropertyEditorRegistry> propertyEditorInitializer,
                  BindHandler defaultBindHandler, BindConstructorProvider constructorProvider) {
        this(
                sources,
                placeholdersResolver,
                (conversionService != null) ? Collections.singletonList(conversionService) : null,
                propertyEditorInitializer,
                defaultBindHandler,
                constructorProvider
        );
    }

    public Binder(Iterable<ConfigurationPropertySource> sources, PlaceholdersResolver placeholdersResolver,
                  List<ConversionService> conversionServices, Consumer<PropertyEditorRegistry> propertyEditorInitializer,
                  BindHandler defaultBindHandler, BindConstructorProvider constructorProvider) {
        Assert.notNull(sources, "Sources must not be null");
        for (ConfigurationPropertySource source : sources) {
            Assert.notNull(source, "Sources must not contain null elements");
        }
        // todo Binder constructor
    }

    public static Binder get(Environment environment) {
        return get(environment, null);
    }

    public static Binder get(Environment environment, BindHandler defaultBindHandler) {
        Iterable<ConfigurationPropertySource> sources = ConfigurationPropertySources.get(environment);
        PropertySourcesPlaceholdersResolver placeholdersResolver = new PropertySourcesPlaceholdersResolver(environment);
        return new Binder(sources, placeholdersResolver, null, null, defaultBindHandler);
    }
}
