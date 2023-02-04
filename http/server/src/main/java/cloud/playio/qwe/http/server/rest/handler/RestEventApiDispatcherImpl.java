package cloud.playio.qwe.http.server.rest.handler;

import cloud.playio.qwe.eventbus.EventMessage;
import cloud.playio.qwe.http.server.handler.EventBusProxyDispatcherImpl;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class RestEventApiDispatcherImpl extends EventBusProxyDispatcherImpl<EventMessage>
    implements RestEventApiDispatcher {

}
