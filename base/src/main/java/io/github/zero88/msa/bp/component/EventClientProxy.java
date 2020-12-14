package io.github.zero88.msa.bp.component;

import io.github.zero88.msa.bp.transport.ProxyService;
import io.github.zero88.msa.bp.event.EventbusClient;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;

import lombok.NonNull;

public interface EventClientProxy extends ProxyService<EventbusClient> {

    static EventClientProxy create(@NonNull Vertx vertx, DeliveryOptions options) {
        return () -> new DefaultEventClient(vertx, options);
    }

    EventbusClient transporter();

}
