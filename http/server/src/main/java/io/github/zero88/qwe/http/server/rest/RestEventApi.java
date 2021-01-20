package io.github.zero88.qwe.http.server.rest;

import java.util.Collection;

import io.github.zero88.qwe.component.SharedDataDelegate;
import io.github.zero88.qwe.http.event.RestEventApiMetadata;
import io.github.zero88.qwe.http.server.handler.RestEventApiDispatcher;
import io.github.zero88.qwe.micro.metadata.ActionMethodMapping;

/**
 * Make a mapping dynamically between {@code HTTP endpoint} and {@code EventBus}
 */
public interface RestEventApi extends ActionMethodMapping, SharedDataDelegate<RestEventApi> {

    RestEventApi initRouter();

    Collection<RestEventApiMetadata> getRestMetadata();

    @SuppressWarnings("unchecked")
    default <T extends RestEventApiDispatcher> Class<T> dispatcher() {
        return (Class<T>) RestEventApiDispatcher.class;
    }

}
