package io.zero88.qwe.http.server.rest.api;

import java.util.Collection;

import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.http.ActionMethodMapping;
import io.zero88.qwe.http.event.RestEventApiMetadata;
import io.zero88.qwe.http.server.rest.handler.RestEventApiDispatcher;

/**
 * Make a mapping dynamically between {@code HTTP endpoint} and {@code EventBus}
 */
public interface RestEventApi extends ActionMethodMapping {

    RestEventApi initRouter();

    Collection<RestEventApiMetadata> getRestMetadata();

    RestEventApi registerProxy(SharedDataLocalProxy proxy);

    @SuppressWarnings("unchecked")
    default <T extends RestEventApiDispatcher> Class<T> dispatcher() {
        return (Class<T>) RestEventApiDispatcher.class;
    }

}
