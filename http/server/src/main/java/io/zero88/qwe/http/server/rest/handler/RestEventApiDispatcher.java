package io.zero88.qwe.http.server.rest.handler;

import java.util.Objects;

import io.github.zero88.repl.ReflectionClass;
import io.zero88.qwe.eventbus.EventMessage;
import io.zero88.qwe.http.server.HttpSystem.ApisSystem;
import io.zero88.qwe.http.server.handler.EventBusProxyDispatcher;
import io.zero88.qwe.http.server.handler.ResponseEventInterceptor;
import io.zero88.qwe.http.server.handler.ResponseInterceptor;

/**
 * Represents for pushing data via {@code EventBus} then listen {@code reply message}
 */
public interface RestEventApiDispatcher extends EventBusProxyDispatcher<EventMessage>, ApisSystem {

    static RestEventApiDispatcher create(Class<RestEventApiDispatcher> cls) {
        if (cls == null) {
            return ReflectionClass.createObject(RestEventApiDispatcherImpl.class);
        }
        return Objects.requireNonNull(ReflectionClass.createObject(cls), "Unable create REST dispatcher");
    }

    @Override
    default ResponseInterceptor<EventMessage> responseInterceptor() {
        return new ResponseEventInterceptor();
    }

    @Override
    default EventMessage convert(EventMessage resp) {
        return resp;
    }

}
