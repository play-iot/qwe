package io.zero88.qwe.event;

import io.github.zero88.exceptions.ReflectionException;
import io.github.zero88.utils.Reflections.ReflectionMethod;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.dto.JsonData.SerializerFunction;
import io.zero88.qwe.event.refl.MethodMeta;
import io.zero88.qwe.exceptions.ImplementationError;
import io.zero88.qwe.exceptions.ServiceNotFoundException;
import io.zero88.qwe.exceptions.ServiceUnavailable;
import io.zero88.qwe.exceptions.UnsupportedException;
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
    public Future<EventMessage> execute(Message message) {
        final EventMessage msg = EventMessage.convert(message);
        final String addr = message.address();
        debug("Received", msg.getAction(), addr);
        return sharedData.getVertx().executeBlocking(h -> h.handle(execute(msg, addr)));
    }

    private Future<EventMessage> execute(EventMessage msg, String address) {
        final EventAction action = msg.getAction();
        debug("Invoking", action, address);
        Future<EventMessage> future;
        try {
            final MethodMeta methodMeta = annotationProcessor().lookup(listener.getClass(), action);
            future = this.executeMethod(methodMeta, paramParser().extract(msg, methodMeta.params()))
                         .map(serializer)
                         .onSuccess(data -> debug("Succeed when handling", action, address))
                         .map(data -> EventMessage.replySuccess(action, data));
        } catch (ImplementationError e) {
            future = Future.failedFuture(new ServiceUnavailable("Service unavailable", e));
        } catch (UnsupportedException e) {
            future = Future.failedFuture(new ServiceNotFoundException("Service not found", e));
        } catch (IllegalArgumentException e) {
            future = Future.failedFuture(e);
        }
        return future.otherwise(t -> {
            debug("Error when handling", action, address);
            return EventMessage.replyError(action, CarlExceptionConverter.friendly(t));
        });
    }

    private Future<Object> executeMethod(MethodMeta methodMeta, Object[] inputs) {
        try {
            final Object response = ReflectionMethod.execute(listener, methodMeta.toMethod(), inputs);
            if (methodMeta.outputIsVoid()) {
                return Future.succeededFuture();
            }
            if (methodMeta.outputIsVertxFuture()) {
                return (Future<Object>) response;
            }
            return Future.succeededFuture(response);
        } catch (ReflectionException e) {
            return Future.failedFuture(e.getCause());
        }
    }

    private void debug(String lifecycleMsg, EventAction action, String address) {
        listener.logger()
                .debug("{} EventAction [{}] in address [{}] and listener[{}]", lifecycleMsg, action, address,
                       listener.getClass().getName());
    }

}
