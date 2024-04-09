package fun.lance.boot.context.event;

import fun.lance.boot.WinterApplication;
import org.springframework.context.ApplicationEvent;

public class WinterApplicationEvent extends ApplicationEvent {

    private final String[] args;

    public WinterApplicationEvent(WinterApplication application, String[] args) {
        super(application);
        this.args = args;
    }

    public WinterApplication getSpringApplication() {
        return (WinterApplication) getSource();
    }

    public final String[] getArgs() {
        return this.args;
    }
}
