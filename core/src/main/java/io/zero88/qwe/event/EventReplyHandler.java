package io.zero88.qwe.event;

import java.util.Objects;

import io.github.zero88.utils.Reflections.ReflectionClass;
import io.vertx.core.eventbus.Message;
import io.zero88.qwe.CarlConverter;

import lombok.NonNull;

@SuppressWarnings("rawtypes")
public interface EventReplyHandler extends CarlConverter<EventMessage, Message> {

    String DEFAULT_SYSTEM = "REPLY";

    static EventReplyHandler create() {
        return new EventReplyHandlerImpl();
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

    default String replySystem() {
        return DEFAULT_SYSTEM;
    }

    EventMessage otherwise(@NonNull Throwable err);

}
