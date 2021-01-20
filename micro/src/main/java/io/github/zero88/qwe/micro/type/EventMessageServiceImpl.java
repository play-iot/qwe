package io.github.zero88.qwe.micro.type;

import io.github.zero88.qwe.component.SharedDataDelegate;
import io.github.zero88.qwe.dto.JsonData;
import io.github.zero88.qwe.micro.metadata.EventMethodDefinition;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceReference;
import io.vertx.servicediscovery.types.AbstractServiceReference;

import lombok.NonNull;

public class EventMessageServiceImpl implements EventMessageService {

    @Override
    public ServiceReference get(Vertx vertx, ServiceDiscovery discovery, Record record, JsonObject configuration) {
        return new EventMessageServiceReference(vertx, discovery, record, configuration);
    }

    static class EventMessageServiceReference extends AbstractServiceReference<EventMessagePusher> {

        private final DeliveryOptions config;
        private final String sharedKey;

        EventMessageServiceReference(@NonNull Vertx vertx, @NonNull ServiceDiscovery discovery, @NonNull Record record,
                                     @NonNull JsonObject config) {
            super(vertx, discovery, record);
            this.sharedKey = config.getString(SHARED_KEY_CONFIG, this.getClass().getName());
            this.config = new DeliveryOptions(config.getJsonObject(DELIVERY_OPTIONS_CONFIG, new JsonObject()));
        }

        @Override
        protected EventMessagePusher retrieve() {
            return new Pusher(SharedDataDelegate.getEventController(vertx, sharedKey),
                              JsonData.from(this.record().getMetadata().getJsonObject(EVENT_METHOD_CONFIG),
                                            EventMethodDefinition.class), config,
                              record().getLocation().getString(Record.ENDPOINT));
        }

    }

}
