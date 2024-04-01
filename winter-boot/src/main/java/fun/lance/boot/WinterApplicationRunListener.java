package fun.lance.boot;

import org.springframework.core.env.ConfigurableEnvironment;

public interface WinterApplicationRunListener {

    default void starting(ConfigurableBootstrapContext bootstrapContext) {
    }

    default void environmentPrepared(ConfigurableBootstrapContext bootstrapContext,
                                     ConfigurableEnvironment environment) {
    }
}
