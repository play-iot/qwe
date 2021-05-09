package io.zero88.qwe.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.eventbus.Message;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.dto.JsonData;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NonNull;

/**
 * Represents for a listener that receives and processes an {@code EventBus} message then response back to a caller
 *
 * @see EBContract
 * @see EventMessage
 * @see EventAction
 * @see EventPattern#REQUEST_RESPONSE
 */
public interface EventListener {

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
     * Fallback json key if output is {@code collection/primitive} value
     *
     * @return fallback json key. Default: {@code data}
     */
    default String fallback() { return "data"; }

    default @NonNull void handle(SharedDataLocalProxy sharedData, Message<Object> msg) {
        new EventListenerExecutorImpl(this, sharedData).execute(msg).onSuccess(r -> msg.reply(r.toJson()));
    }

}
