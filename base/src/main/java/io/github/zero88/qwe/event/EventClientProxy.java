package io.github.zero88.qwe.event;

import io.github.zero88.qwe.transport.ProxyService;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;

import lombok.NonNull;

public interface EventClientProxy extends ProxyService<EventbusClient> {

    static EventClientProxy create(@NonNull Vertx vertx, DeliveryOptions options) {
        return () -> EventbusClient.create(vertx, options);
    }

    EventbusClient transporter();

}
