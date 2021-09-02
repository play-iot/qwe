package io.zero88.qwe.eventbus;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.zero88.repl.ReflectionClass;
import io.vertx.core.eventbus.Message;
import io.zero88.qwe.HasLogger;
import io.zero88.qwe.QWEConverter;
import io.zero88.qwe.dto.ErrorMessage;
import io.zero88.qwe.eventbus.EventBusLogSystem.EventReplyLogSystem;

import lombok.NonNull;

@SuppressWarnings("rawtypes")
public interface EventBusReplyHandler extends QWEConverter<EventMessage, Message>, HasLogger, EventReplyLogSystem {

    static EventBusReplyHandler create() {
        return new EventBusReplyHandlerImpl();
    }

    static EventBusReplyHandler create(Class<EventBusReplyHandler> clazz) {
        return Objects.isNull(clazz) ? create() : ReflectionClass.createObject(clazz);
    }

    @Override
    default Logger logger() {
        return LoggerFactory.getLogger(EventBusReplyHandler.class);
    }

    @Override
    default Class<EventMessage> fromClass() {
        return EventMessage.class;
    }

    @Override
    default Class<Message> toClass() {
        return Message.class;
    }

    @Override
    default Message from(EventMessage eventMessage) {
        return null;
    }

    /**
     * Load event context to set up an event reply handler
     *
     * @param address event listener address
     * @param action  event listener action
     * @return a reference to this for fluent API
     */
    EventBusReplyHandler loadContext(String address, EventAction action);

    /**
     * Convert throwable to Event Reply Message
     *
     * @param error an error
     * @return an event message
     * @see EventMessage#replyError(EventAction, ErrorMessage)
     * @see EventMessage#replyError(EventAction, Throwable)
     */
    EventMessage otherwise(@NonNull Throwable error);

}
