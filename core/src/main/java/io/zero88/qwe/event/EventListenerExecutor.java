package io.zero88.qwe.event;

import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.zero88.qwe.HasSharedData;
import io.zero88.qwe.event.refl.EventAnnotationProcessor;
import io.zero88.qwe.event.refl.EventParameterParser;

import lombok.NonNull;

public interface EventListenerExecutor extends HasSharedData {

    @NonNull EventListener listener();

    default EventReplyHandler converter() {
        return EventReplyHandler.create();
    }

    default EventAnnotationProcessor annotationProcessor() {
        return EventAnnotationProcessor.create();
    }

    default EventParameterParser paramParser() {
        return EventParameterParser.create(sharedData(), listener().mapper());
    }

    Future<EventMessage> execute(Message message);

}
