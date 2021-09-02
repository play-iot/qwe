package io.zero88.qwe.eventbus;

import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.zero88.qwe.HasSharedData;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.eventbus.refl.EventAnnotationProcessor;
import io.zero88.qwe.eventbus.refl.EventParameterParser;

import lombok.NonNull;

public interface EventBusListenerExecutor extends HasSharedData {

    static EventBusListenerExecutor create(EventBusListener listener, SharedDataLocalProxy sharedData){
        return new EventBusListenerExecutorImpl(listener, sharedData);
    }

    @NonNull EventBusListener listener();

    default EventAnnotationProcessor annotationProcessor() {
        return EventAnnotationProcessor.create();
    }

    default EventParameterParser paramParser() {
        return EventParameterParser.create(sharedData(), listener().mapper());
    }

    Future<EventMessage> execute(Message message);

}
