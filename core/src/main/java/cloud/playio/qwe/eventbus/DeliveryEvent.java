package cloud.playio.qwe.eventbus;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public final class DeliveryEvent {

    private String address;
    private EventAction action;
    private EventPattern pattern = EventPattern.REQUEST_RESPONSE;
    private boolean useRequestData = true;

}
