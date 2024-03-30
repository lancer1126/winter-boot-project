package fun.lance.boot;

import org.springframework.core.env.ConfigurableEnvironment;

public interface ApplicationContextFactory {
    ApplicationContextFactory DEFAULT = new DefaultApplicationContextFactory();

    default ConfigurableEnvironment createEnvironment(WebApplicationType webApplicationType) {
        return null;
    }
}
