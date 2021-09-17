package io.zero88.qwe.http.server.rest.api;

import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.eventbus.EventPattern;
import io.zero88.qwe.http.ActionMethodMapping;
import io.zero88.qwe.http.EventMethodDefinition;
import io.zero88.qwe.http.server.RouterConfig;
import io.zero88.qwe.http.server.rest.handler.RestEventApiDispatcher;

public interface IRestEventApi<C extends RouterConfig> {

    IRestEventApi<C> setup(C config, SharedDataLocalProxy sharedData);

    String address();

    EventMethodDefinition getDefinition();

    default EventPattern pattern() {
        return EventPattern.REQUEST_RESPONSE;
    }

    default ActionMethodMapping mapping() {
        return ActionMethodMapping.CRUD_MAP;
    }

    default <T extends RestEventApiDispatcher> Class<T> dispatcher() {
        return null;
    }

}
