package fun.lance.boot.web.servlet.context;

import fun.lance.boot.ApplicationContextFactory;
import fun.lance.boot.WebApplicationType;
import org.springframework.core.env.ConfigurableEnvironment;

public class ServletWebServerApplicationContextFactory implements ApplicationContextFactory {

    @Override
    public ConfigurableEnvironment createEnvironment(WebApplicationType webApplicationType) {
        return (webApplicationType != WebApplicationType.SERVLET) ? null : new ApplicationServletEnvironment();
    }
}
