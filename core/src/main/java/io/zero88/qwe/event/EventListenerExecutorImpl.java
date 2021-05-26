package io.zero88.qwe.event;

import io.github.zero88.exceptions.ReflectionException;
import io.github.zero88.utils.Reflections.ReflectionMethod;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.dto.JsonData.SerializerFunction;
import io.zero88.qwe.event.refl.MethodMeta;
import io.zero88.qwe.exceptions.ImplementationError;
import io.zero88.qwe.exceptions.QWEExceptionConverter;
import io.zero88.qwe.exceptions.ServiceNotFoundException;
import io.zero88.qwe.exceptions.ServiceUnavailable;
import io.zero88.qwe.exceptions.UnsupportedException;

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
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Future<EventMessage> execute(Message message) {
        final EventMessage msg = EventMessage.convert(message);
        final String addr = message.address();
        debug("Received", msg.getAction(), addr);
        return sharedData.getVertx().executeBlocking(promise -> execute(msg, addr).onComplete(promise));
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
            future = Future.failedFuture(new ServiceUnavailable(e));
        } catch (UnsupportedException e) {
            future = Future.failedFuture(new ServiceNotFoundException(e));
        } catch (IllegalArgumentException e) {
            future = Future.failedFuture(e);
        }
        return future.otherwise(t -> {
            debug("Error when handling", action, address, t);
            return EventMessage.replyError(action, QWEExceptionConverter.friendly(t));
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
        debug(lifecycleMsg, action, address, null);
    }

    private void debug(String lifecycleMsg, EventAction action, String address, Throwable t) {
        listener.logger().debug("{} EventAction [{}] in address [{}]", lifecycleMsg, action, address, t);
    }

}
