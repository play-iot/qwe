package io.zero88.qwe.event;

import io.zero88.qwe.transport.ProxyService;

public interface EventbusProxy extends ProxyService<EventbusClient> {

    EventbusClient transporter();

}
