package io.zero88.qwe.event;

import io.github.classgraph.MethodInfo;
import io.github.zero88.utils.Reflections.ReflectionMethod;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.zero88.qwe.component.SharedDataLocalProxy;
import io.zero88.qwe.dto.JsonData.SerializerFunction;
import io.zero88.qwe.exceptions.DesiredException;
import io.zero88.qwe.exceptions.ImplementationError;
import io.zero88.qwe.exceptions.converter.CarlExceptionConverter;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
class EventListenerExecutorImpl implements EventListenerExecutor {

    private final EventListener listener;
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
        final EventAnnotationProcessor processor = annotationProcessor();
        try {
            final MethodInfo methodInfo = processor.find(listener.getClass(), action);
            final Object[] inputs = processor.extract(listener.mapper(), message, methodInfo.getParameterInfo());
            final Object response = ReflectionMethod.execute(listener, methodInfo.loadClassAndGetMethod(), inputs);
            return convertResult(response).map(data -> EventMessage.replySuccess(action, data))
                                          .otherwise(t -> convertError(t, action));
        } catch (Exception e) {
            return Future.succeededFuture(convertError(e, action));
        } finally {
            processor.close();
        }
    }

    @SuppressWarnings("unchecked")
    private Future<JsonObject> convertResult(Object response) {
        if (response instanceof Future) {
            return ((Future) response).map(serializer);
        }
        return Future.succeededFuture(serializer.apply(response));
    }

    private EventMessage convertError(Throwable throwable, EventAction action) {
        if (throwable instanceof DesiredException) {
            listener().logger().debug("Failed when handle event [{}]", action, throwable);
        } else if (throwable instanceof ImplementationError) {
            listener().logger().error("Failed when handle event [{}]", action, throwable);
        } else {
            listener().logger().warn("Failed when handle event [{}]", action, throwable);
        }
        final String overrideMsg = throwable instanceof ImplementationError
                                   ? "No reply from event [" + action + "]"
                                   : "";
        return EventMessage.replyError(action, CarlExceptionConverter.friendly(throwable, overrideMsg));
    }

}
