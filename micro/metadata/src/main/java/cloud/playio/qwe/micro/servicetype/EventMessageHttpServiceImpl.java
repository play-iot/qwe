package cloud.playio.qwe.micro.servicetype;

import java.util.Optional;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceReference;
import io.vertx.servicediscovery.types.AbstractServiceReference;
import cloud.playio.qwe.SharedDataLocalProxy;

import lombok.NonNull;

public final class EventMessageHttpServiceImpl implements EventMessageHttpService {

    @Override
    public ServiceReference get(Vertx vertx, ServiceDiscovery discovery, Record record, JsonObject configuration) {
        return new EventMessageServiceReference(vertx, discovery, record, configuration);
    }

    static final class EventMessageServiceReference extends AbstractServiceReference<EventMessagePusher> {

        private final DeliveryOptions config;
        private final String sharedKey;

        EventMessageServiceReference(@NonNull Vertx vertx, @NonNull ServiceDiscovery discovery, @NonNull Record record,
                                     @NonNull JsonObject config) {
            super(vertx, discovery, record);
            this.sharedKey = config.getString(SHARED_KEY_CONFIG, this.getClass().getName());
            this.config = Optional.ofNullable(config.getJsonObject(DELIVERY_OPTIONS_CONFIG))
                                  .map(DeliveryOptions::new)
                                  .orElseGet(DeliveryOptions::new);
        }

        @Override
        protected EventMessagePusher retrieve() {
            return new Pusher(SharedDataLocalProxy.create(vertx, sharedKey), config,
                              record().getLocation().getString(Record.ENDPOINT));
        }

    }

}
