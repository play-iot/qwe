package cloud.playio.qwe.eventbus;

import cloud.playio.qwe.transport.ProxyService;

public interface EventBusProxy extends ProxyService<EventBusClient> {

    EventBusClient transporter();

}
