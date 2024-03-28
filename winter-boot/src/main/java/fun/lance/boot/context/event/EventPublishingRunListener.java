package fun.lance.boot.context.event;

import fun.lance.boot.WinterApplicationRunListener;
import org.springframework.core.Ordered;

public class EventPublishingRunListener implements WinterApplicationRunListener, Ordered {

    @Override
    public int getOrder() {
        return 0;
    }
}
