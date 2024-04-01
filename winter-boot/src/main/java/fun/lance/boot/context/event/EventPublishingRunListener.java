package fun.lance.boot.context.event;

import fun.lance.boot.ConfigurableBootstrapContext;
import fun.lance.boot.WinterApplication;
import fun.lance.boot.WinterApplicationRunListener;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

public class EventPublishingRunListener implements WinterApplicationRunListener, Ordered {

    private final WinterApplication application;

    private final String[] args;

    private final SimpleApplicationEventMulticaster initialMulticaster;

    EventPublishingRunListener(WinterApplication application, String[] args) {
        this.application = application;
        this.args = args;
        this.initialMulticaster = new SimpleApplicationEventMulticaster();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext, ConfigurableEnvironment environment) {

    }

    private void multicastInitialEvent(ApplicationEvent event) {

    }

    private void refreshApplicationListeners() {
        this.application.getListeners().forEach(this.initialMulticaster::addApplicationListener);
    }
}
