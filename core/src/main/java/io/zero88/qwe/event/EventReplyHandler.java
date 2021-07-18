package io.zero88.qwe.event;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.zero88.repl.ReflectionClass;
import io.vertx.core.eventbus.Message;
import io.zero88.qwe.HasLogger;
import io.zero88.qwe.QWEConverter;
import io.zero88.qwe.dto.ErrorMessage;
import io.zero88.qwe.event.EventLogSystem.EventReplyLogSystem;

import lombok.NonNull;

@SuppressWarnings("rawtypes")
public interface EventReplyHandler extends QWEConverter<EventMessage, Message>, HasLogger, EventReplyLogSystem {

    static EventReplyHandler create() {
        return new EventReplyHandlerImpl();
    }

    static EventReplyHandler create(Class<EventReplyHandler> clazz) {
        return Objects.isNull(clazz) ? create() : ReflectionClass.createObject(clazz);
    }

    @Override
    default Logger logger() {
        return LoggerFactory.getLogger(EventReplyHandler.class);
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
     * Load event context to setup an event reply handler
     *
     * @param address event listener address
     * @param action  event listener action
     * @return a reference to this for fluent API
     */
    EventReplyHandler loadContext(String address, EventAction action);

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
