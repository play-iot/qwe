package io.zero88.qwe.event;

import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.zero88.utils.Strings;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import io.zero88.qwe.SharedDataLocalProxy;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@RequiredArgsConstructor
final class EventBusClientImpl implements EventBusClient {

    static final Logger LOGGER = LoggerFactory.getLogger(EventBusClient.class);

    @Getter
    @NonNull
    private final SharedDataLocalProxy sharedData;
    private final Class<EventReplyHandler> replyHandlerClass;

    @Override
    public EventBusClient send(@NonNull String address, @NonNull EventMessage message, DeliveryOptions options) {
        unwrap().send(address, message.toJson(), getOpts(options));
        return this;
    }

    @Override
    public Future<EventMessage> request(@NonNull String address, @NonNull EventMessage message,
                                        DeliveryOptions options) {
        EventReplyHandler replyHandler = EventReplyHandler.create(replyHandlerClass)
                                                          .loadContext(address, message.getAction());
        return unwrap().request(address, message.toJson(), getOpts(options))
                       .map(replyHandler::to)
                       .otherwise(replyHandler::otherwise);
    }

    @Override
    public EventBusClient publish(@NonNull String address, @NonNull EventMessage message, DeliveryOptions options) {
        unwrap().publish(address, message.toJson(), getOpts(options));
        return this;
    }

    @Override
    public EventBusClient register(String address, boolean local, @NonNull EventListener listener) {
        LOGGER.info("Registering {} Event Listener '{}' | Address '{}'...", local ? "Local" : "Cluster",
                    listener.getClass().getName(), Strings.requireNotBlank(address));
        if (local) {
            unwrap().localConsumer(address, msg -> listener.handle(sharedData, msg));
        } else {
            unwrap().consumer(address, msg -> listener.handle(sharedData, msg));
        }
        return this;
    }

    DeliveryOptions getOpts(DeliveryOptions opts) {
        if (Objects.nonNull(opts)) {
            return opts;
        }
        final EventBusDeliveryOption option = sharedData.getData(SharedDataLocalProxy.EVENTBUS_DELIVERY_OPTION);
        return Optional.ofNullable(option).map(EventBusDeliveryOption::get).orElseGet(DeliveryOptions::new);
    }

}
