package io.github.zero88.msa.blueprint.event;

import java.util.Collection;
import java.util.function.Function;

import io.github.zero88.msa.blueprint.dto.JsonData;
import io.reactivex.Single;
import io.vertx.core.eventbus.Message;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NonNull;

/**
 * Handlers a received {@code Eventbus} message.
 *
 * @see EventContractor
 * @see EventMessage
 * @see EventAction
 * @see EventPattern#REQUEST_RESPONSE
 */
public interface EventListener extends Function<Message<Object>, Single<EventMessage>> {

    /**
     * Available events that this handler can process
     *
     * @return list of possible events
     */
    @NonNull Collection<EventAction> getAvailableEvents();

    default Logger logger() {
        return LoggerFactory.getLogger(this.getClass());
    }

    /**
     * Jackson Object mapper for serialize/deserialize data
     *
     * @return Object mapper. Default: {@link JsonData#MAPPER}
     */
    default ObjectMapper mapper() { return JsonData.MAPPER; }

    /**
     * Fallback json key if output is {@code collection/primitive}  value
     *
     * @return fallback json key. Default: {@code data}
     */
    default String fallback() { return "data"; }

    @Override
    default Single<EventMessage> apply(Message<Object> message) {
        EventMessage msg = EventMessage.tryParse(message.body());
        return new AnnotationHandler<>(this).execute(msg).doOnSuccess(data -> message.reply(data.toJson()));
    }

}
