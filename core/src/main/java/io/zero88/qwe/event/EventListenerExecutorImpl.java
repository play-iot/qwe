package io.zero88.qwe.event;

import io.github.zero88.utils.Reflections.ReflectionMethod;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.dto.JsonData.SerializerFunction;
import io.zero88.qwe.event.refl.EventAnnotationProcessor.MethodMeta;
import io.zero88.qwe.exceptions.DesiredException;
import io.zero88.qwe.exceptions.ImplementationError;
import io.zero88.qwe.exceptions.converter.CarlExceptionConverter;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
class EventListenerExecutorImpl implements EventListenerExecutor {

    @Getter
    private final EventListener listener;
    @Getter
    private final SharedDataLocalProxy sharedData;
    private final SerializerFunction serializer;

    EventListenerExecutorImpl(EventListener listener, SharedDataLocalProxy sharedData) {
        this.listener = listener;
        this.sharedData = sharedData;
        this.serializer = SerializerFunction.builder()
                                            .mapper(listener.mapper())
                                            .backupKey(listener.fallback())
                                            .lenient(true)
                                            .build();
    }

    @Override
    @SuppressWarnings( {"rawtypes", "unchecked"})
    public Future<EventMessage> execute(Message msg) {
        final EventMessage message = EventMessage.convert(msg);
        final EventAction action = message.getAction();
        listener.logger().debug("Executing action [{}] in listener [{}]", action, listener.getClass().getName());
        return sharedData.getVertx().executeBlocking(event -> event.handle(execute(message, action)));
    }

    private Future<EventMessage> execute(EventMessage message, EventAction action) {
        try {
            final MethodMeta methodMeta = annotationProcessor().scan(listener.getClass(), action);
            return this.executeMethod(methodMeta, paramParser().extract(message, methodMeta.params()))
                       .map(serializer)
                       .map(data -> EventMessage.replySuccess(action, data))
                       .otherwise(t -> convertError(t, action));
        } catch (Exception e) {
            return Future.succeededFuture(convertError(e, action));
        }
    }

    private Future<Object> executeMethod(MethodMeta methodMeta, Object[] inputs) {
        final Object response = ReflectionMethod.execute(listener, methodMeta.toMethod(), inputs);
        if (methodMeta.outputIsVoid()) {
            return Future.succeededFuture();
        }
        if (methodMeta.outputIsVertxFuture()) {
            return (Future<Object>) response;
        }
        return Future.succeededFuture(response);
    }

    private EventMessage convertError(Throwable throwable, EventAction action) {
        if (throwable instanceof DesiredException) {
            listener().logger().debug("Failed when handling event [{}]", action, throwable);
        } else if (throwable instanceof ImplementationError) {
            listener().logger().error("Failed when handling event [{}]", action, throwable);
        } else {
            listener().logger().warn("Failed when handling event [{}]", action, throwable);
        }
        final String overrideMsg = throwable instanceof ImplementationError
                                   ? "No reply from event [" + action + "]"
                                   : "";
        return EventMessage.replyError(action, CarlExceptionConverter.friendly(throwable, overrideMsg));
    }

}
