package fun.lance.boot;

import org.apache.commons.logging.Log;
import org.springframework.core.metrics.ApplicationStartup;

import java.util.List;

public class WinterApplicationRunListeners {

    private final Log log;
    private final List<WinterApplicationRunListener> listeners;
    private final ApplicationStartup applicationStartup;

    public WinterApplicationRunListeners(Log log, List<WinterApplicationRunListener> listeners, ApplicationStartup applicationStartup) {
        this.log = log;
        this.listeners = List.copyOf(listeners);
        this.applicationStartup = applicationStartup;
    }
}
