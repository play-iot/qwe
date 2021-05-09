package io.zero88.qwe.event;

import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.zero88.qwe.HasSharedData;

public interface EventListenerExecutor extends HasSharedData {

    EventListener listener();

    default EventReplyHandler converter() {
        return EventReplyHandler.create();
    }

    default EventAnnotationProcessor annotationProcessor() {
        return EventAnnotationProcessor.create();
    }

    Future<EventMessage> execute(Message message);

}
