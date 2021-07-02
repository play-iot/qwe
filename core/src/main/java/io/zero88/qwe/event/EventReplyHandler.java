package io.zero88.qwe.event;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.zero88.utils.Reflections.ReflectionClass;
import io.vertx.core.eventbus.Message;
import io.zero88.qwe.HasLogger;
import io.zero88.qwe.QWEConverter;

import lombok.NonNull;

@SuppressWarnings("rawtypes")
public interface EventReplyHandler extends QWEConverter<EventMessage, Message>, HasLogger {

    String DEFAULT_SYSTEM = "Backend Service";

    static EventReplyHandler create() {
        return new EventReplyHandlerImpl();
    }

    /**
     * Create {@link EventReplyHandler} instance
     *
     * @param backendFunction backend function name for logging
     * @return new instance
     */
    static EventReplyHandler create(String backendFunction) {
        return new EventReplyHandlerImpl(backendFunction);
    }

    static EventReplyHandler create(Class<EventReplyHandler> clazz) {
        return Objects.isNull(clazz) ? create() : ReflectionClass.createObject(clazz);
    }

    EventReplyHandler loadContext(String address, EventAction action);

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

    default String replySystem() {
        return DEFAULT_SYSTEM;
    }

    @Override
    default Logger logger() {
        return LoggerFactory.getLogger(EventReplyHandler.class);
    }

    EventMessage otherwise(@NonNull Throwable err);

}
