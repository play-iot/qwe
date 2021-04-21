package io.zero88.qwe.event;

import java.util.function.Supplier;

import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.shareddata.Shareable;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class EventbusDeliveryOption implements Shareable, Supplier<DeliveryOptions> {

    private final DeliveryOptions options;

    @Override
    public Shareable copy() {
        return new EventbusDeliveryOption(new DeliveryOptions(this.options));
    }

    @Override
    public DeliveryOptions get() {
        return options;
    }

}
