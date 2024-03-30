package fun.lance.boot;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class DefaultApplicationContextFactory implements ApplicationContextFactory {

    @Override
    public ConfigurableEnvironment createEnvironment(WebApplicationType webApplicationType) {
        return getFromSpringFactories(webApplicationType, ApplicationContextFactory::createEnvironment, null);
    }

    private <T> T getFromSpringFactories(WebApplicationType webApplicationType,
                                         BiFunction<ApplicationContextFactory, WebApplicationType, T> action,
                                         Supplier<T> defaultResult) {
        List<ApplicationContextFactory> factories = SpringFactoriesLoader
                .loadFactories(ApplicationContextFactory.class, getClass().getClassLoader());
        for (ApplicationContextFactory candidate : factories) {
            T result = action.apply(candidate, webApplicationType);
            if (result != null) {
                return  result;
            }
        }
        return (defaultResult != null) ? defaultResult.get() : null;
    }
}
