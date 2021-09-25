package io.zero88.qwe.http.server.rest.handler;

import io.zero88.qwe.eventbus.EventMessage;
import io.zero88.qwe.http.server.handler.EventBusProxyDispatcherImpl;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class RestEventApiDispatcherImpl extends EventBusProxyDispatcherImpl<EventMessage>
    implements RestEventApiDispatcher {

}
