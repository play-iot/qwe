package io.github.zero88.msa.blueprint.component;

import io.github.zero88.msa.blueprint.event.EventbusClient;
import io.github.zero88.msa.blueprint.transport.ProxyService;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;

import lombok.NonNull;

public interface EventClientProxy extends ProxyService<EventbusClient> {

    static EventClientProxy create(@NonNull Vertx vertx, DeliveryOptions options) {
        return () -> new DefaultEventClient(vertx, options);
    }

    EventbusClient transporter();

}
