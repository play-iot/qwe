package io.zero88.qwe.http.server.handler;

import io.zero88.qwe.auth.ReqAuthDefinition;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public abstract class EventBusProxyDispatcherImpl<T> implements EventBusProxyDispatcher<T> {

    private HttpEBDispatcher dispatcher;
    private AuthInterceptor authInterceptor;

    public EventBusProxyDispatcherImpl<T> setup(HttpEBDispatcher dispatcher, ReqAuthDefinition authDefinition) {
        this.dispatcher = dispatcher;
        this.authInterceptor = AuthInterceptor.create(authDefinition);
        return this;
    }

}
