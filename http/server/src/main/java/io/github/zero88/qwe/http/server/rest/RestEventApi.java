package io.github.zero88.qwe.http.server.rest;

import java.util.Collection;

import io.github.zero88.qwe.component.SharedDataLocalProxy;
import io.github.zero88.qwe.http.event.RestEventApiMetadata;
import io.github.zero88.qwe.http.server.handler.RestEventApiDispatcher;
import io.github.zero88.qwe.micro.http.ActionMethodMapping;

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
