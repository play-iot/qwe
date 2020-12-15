package io.github.zero88.msa.bp.component;

import java.util.Objects;

import io.github.zero88.msa.bp.event.EventListener;
import io.github.zero88.msa.bp.event.EventMessage;
import io.github.zero88.msa.bp.event.EventPattern;
import io.github.zero88.msa.bp.event.EventbusClient;
import io.github.zero88.msa.bp.event.ReplyEventHandler;
import io.github.zero88.msa.bp.utils.ExecutorHelpers;
import io.github.zero88.utils.Strings;
import io.reactivex.Single;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.shareddata.Shareable;

import lombok.Getter;
import lombok.NonNull;

/**
 * Represents {@code Eventbus} controller to {@code send}, {@code publish}, {@code register} event
 *
 * @see EventMessage
 */
final class DefaultEventClient implements EventbusClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventbusClient.class);

    @Getter
    private final Vertx vertx;
    private final DeliveryOptions deliveryOptions;

    DefaultEventClient(@NonNull Vertx vertx) {
        this(vertx, null);
    }

    DefaultEventClient(@NonNull Vertx vertx, DeliveryOptions deliveryOptions) {
        this.vertx = vertx;
        this.deliveryOptions = Objects.nonNull(deliveryOptions) ? deliveryOptions : new DeliveryOptions();
    }

    @Override
    public Single<EventMessage> request(@NonNull String address, @NonNull EventMessage message,
                                        DeliveryOptions deliveryOptions) {
        return Single.create(emitter -> request(address, message, ReplyEventHandler.builder()
                                                                                   .action(message.getAction())
                                                                                   .address(address)
                                                                                   .success(emitter::onSuccess)
                                                                                   .exception(emitter::onError)
                                                                                   .build()));
    }

    /**
     * {@inheritDoc}
     */
    public void fire(String address, @NonNull EventPattern pattern, @NonNull JsonObject data,
                     Handler<AsyncResult<Message<Object>>> replyHandler, DeliveryOptions deliveryOptions) {
        DeliveryOptions options = Objects.nonNull(deliveryOptions) ? deliveryOptions : this.deliveryOptions;
        Strings.requireNotBlank(address);
        LOGGER.debug("Eventbus::Fire:Address: {} - Pattern: {}", address, pattern);
        if (pattern == EventPattern.PUBLISH_SUBSCRIBE) {
            vertx.eventBus().publish(address, data, options);
        }
        if (pattern == EventPattern.POINT_2_POINT) {
            vertx.eventBus().send(address, data, options);
        }
        if (pattern == EventPattern.REQUEST_RESPONSE) {
            Objects.requireNonNull(replyHandler, "Must provide message reply handler");
            vertx.eventBus().send(address, data, options, replyHandler);
        }
    }

    /**
     * {@inheritDoc}
     */
    public EventbusClient register(String address, boolean local, @NonNull EventListener listener) {
        LOGGER.info("Registering {} Event Listener '{}' | Address '{}'...", local ? "Local" : "Cluster",
                    listener.getClass().getName(), Strings.requireNotBlank(address));
        final Handler<Message<Object>> msgHandler = msg -> ExecutorHelpers.blocking(vertx, listener.apply(msg))
                                                                          .subscribe();
        if (local) {
            vertx.eventBus().localConsumer(address, msgHandler);
        } else {
            vertx.eventBus().consumer(address, msgHandler);
        }
        return this;
    }

    @Override
    public Shareable copy() {
        return new DefaultEventClient(vertx, new DeliveryOptions(deliveryOptions.toJson()));
    }

}
