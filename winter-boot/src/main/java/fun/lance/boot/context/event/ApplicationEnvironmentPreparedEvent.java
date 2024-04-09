package fun.lance.boot.context.event;

import fun.lance.boot.ConfigurableBootstrapContext;
import fun.lance.boot.WinterApplication;
import org.springframework.core.env.ConfigurableEnvironment;

public class ApplicationEnvironmentPreparedEvent extends WinterApplicationEvent {

    private final ConfigurableBootstrapContext bootstrapContext;

    private final ConfigurableEnvironment environment;

    /**
     * Create a new {@link ApplicationEnvironmentPreparedEvent} instance.
     * @param bootstrapContext the bootstrap context
     * @param application the current application
     * @param args the arguments the application is running with
     * @param environment the environment that was just created
     */
    public ApplicationEnvironmentPreparedEvent(ConfigurableBootstrapContext bootstrapContext,
                                               WinterApplication application,
                                               String[] args,
                                               ConfigurableEnvironment environment) {
        super(application, args);
        this.bootstrapContext = bootstrapContext;
        this.environment = environment;
    }

    public ConfigurableBootstrapContext getBootstrapContext() {
        return this.bootstrapContext;
    }

    public ConfigurableEnvironment getEnvironment() {
        return this.environment;
    }
}
