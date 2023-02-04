package cloud.playio.qwe.eventbus;

import io.vertx.core.eventbus.Message;
import cloud.playio.qwe.HasLogger;
import cloud.playio.qwe.SharedDataLocalProxy;
import cloud.playio.qwe.dto.JsonData;
import cloud.playio.qwe.eventbus.EventBusLogSystem.EventListenerLogSystem;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NonNull;

/**
 * Represents for a {@code listener} that receives and processes an {@code EventBus} message then response back to a
 * caller.
 *
 * @see EBContract
 * @see EventMessage
 * @see EventAction
 * @see EventPattern#REQUEST_RESPONSE
 */
public interface EventListener extends HasLogger, EventListenerLogSystem {

    /**
     * Jackson Object mapper for serialize/deserialize data
     *
     * @return Object mapper. Default: {@link JsonData#MAPPER}
     */
    default ObjectMapper mapper() {return JsonData.MAPPER;}

    /**
     * Fallback json key if output is {@code collection/primitive} value
     *
     * @return fallback json key. Default: {@code data}
     */
    default String fallback() {return JsonData.SUCCESS_KEY;}

    default EventListenerExecutor executor(SharedDataLocalProxy sharedData) {
        return EventListenerExecutor.create(this, sharedData);
    }

    default @NonNull void handle(SharedDataLocalProxy sharedData, Message<Object> msg) {
        executor(sharedData).execute(msg).onComplete(ar -> {
            if (ar.succeeded()) {
                msg.reply(ar.result().toJson());
            } else {
                msg.reply(EventMessage.replyError(EventAction.UNKNOWN, ar.cause()).toJson());
            }
        });
    }

}
