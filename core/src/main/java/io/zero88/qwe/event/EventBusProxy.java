package io.zero88.qwe.event;

import io.zero88.qwe.transport.ProxyService;

public interface EventBusProxy extends ProxyService<EventBusClient> {

    EventBusClient transporter();

}
