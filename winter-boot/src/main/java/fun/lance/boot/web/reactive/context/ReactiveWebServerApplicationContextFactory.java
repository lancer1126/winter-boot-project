package fun.lance.boot.web.reactive.context;

import fun.lance.boot.ApplicationContextFactory;
import fun.lance.boot.WebApplicationType;
import org.springframework.core.env.ConfigurableEnvironment;

public class ReactiveWebServerApplicationContextFactory implements ApplicationContextFactory {

    @Override
    public ConfigurableEnvironment createEnvironment(WebApplicationType webApplicationType) {
        return (webApplicationType != WebApplicationType.REACTIVE) ? null : new ApplicationReactiveWebEnvironment();
    }
}
