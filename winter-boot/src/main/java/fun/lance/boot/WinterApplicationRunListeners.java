package fun.lance.boot;

import org.apache.commons.logging.Log;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.core.metrics.StartupStep;

import java.util.List;
import java.util.function.Consumer;

public class WinterApplicationRunListeners {

    private final Log log;
    private final List<WinterApplicationRunListener> listeners;
    private final ApplicationStartup applicationStartup;

    public WinterApplicationRunListeners(Log log, List<WinterApplicationRunListener> listeners, ApplicationStartup applicationStartup) {
        this.log = log;
        this.listeners = List.copyOf(listeners);
        this.applicationStartup = applicationStartup;
    }

    void starting(ConfigurableBootstrapContext bootstrapContext, Class<?> mainApplicationClass) {
        doWithListeners("winter.boot.application.starting", (listener) -> listener.starting(bootstrapContext),
                (step) -> {
                    if (mainApplicationClass != null) {
                        step.tag("mainApplicationClass", mainApplicationClass.getName());
                    }
                });
    }

    void environmentPrepared(ConfigurableBootstrapContext bootstrapContext, ConfigurableEnvironment environment) {
        doWithListeners("winter.boot.application.environment-prepared",
                (listener) -> listener.environmentPrepared(bootstrapContext, environment));
    }

    private void doWithListeners(String stepName, Consumer<WinterApplicationRunListener> listenerAction) {
        doWithListeners(stepName, listenerAction, null);
    }

    private void doWithListeners(String stepName, Consumer<WinterApplicationRunListener> listenerAction,
                                 Consumer<StartupStep> stepAction) {
        StartupStep step = this.applicationStartup.start(stepName);
        this.listeners.forEach(listenerAction);
        if (stepAction != null) {
            stepAction.accept(step);
        }
        step.end();
    }
}
