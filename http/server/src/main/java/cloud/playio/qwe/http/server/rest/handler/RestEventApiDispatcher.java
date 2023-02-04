package cloud.playio.qwe.http.server.rest.handler;

import java.util.Objects;

import io.github.zero88.repl.ReflectionClass;
import cloud.playio.qwe.http.server.HttpSystem.ApisSystem;
import cloud.playio.qwe.http.server.handler.EventBusProxyDispatcher.EventMessageResponseDispatcher;

/**
 * Represents for pushing data via {@code EventBus} then listen {@code reply message}
 */
public interface RestEventApiDispatcher extends EventMessageResponseDispatcher, ApisSystem {

    static RestEventApiDispatcher create(Class<RestEventApiDispatcher> cls) {
        if (cls == null) {
            return ReflectionClass.createObject(RestEventApiDispatcherImpl.class);
        }
        return Objects.requireNonNull(ReflectionClass.createObject(cls), "Unable create REST dispatcher");
    }

}
