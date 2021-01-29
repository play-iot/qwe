package io.github.zero88.qwe.http.server;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.zero88.qwe.http.event.WebSocketServerEventMetadata;
import io.github.zero88.qwe.http.server.rest.api.RestApi;
import io.github.zero88.qwe.http.server.rest.api.RestEventApi;

import lombok.Getter;

//TODO: Use Builder: WebsocketEventBuilder, RestEventApisBuilder
@Getter
public final class HttpServerRouter {

    private final Set<Class<? extends RestApi>> restApiClasses = new HashSet<>();
    private final Set<Class<? extends RestEventApi>> restEventApiClasses = new HashSet<>();
    private final Set<WebSocketServerEventMetadata> webSocketEvents = new HashSet<>();
    private final Set<Class<? extends RestEventApi>> gatewayApiClasses = new HashSet<>();

    @SafeVarargs
    public final HttpServerRouter registerApi(Class<? extends RestApi>... apiClass) {
        restApiClasses.addAll(Arrays.stream(apiClass).filter(Objects::nonNull).collect(Collectors.toList()));
        return this;
    }

    @SafeVarargs
    public final HttpServerRouter registerEventBusApi(Class<? extends RestEventApi>... eventBusApiClass) {
        restEventApiClasses.addAll(
            Arrays.stream(eventBusApiClass).filter(Objects::nonNull).collect(Collectors.toList()));
        return this;
    }

    @SafeVarargs
    public final HttpServerRouter registerGatewayApi(Class<? extends RestEventApi>... gatewayApiClass) {
        gatewayApiClasses.addAll(Arrays.stream(gatewayApiClass).filter(Objects::nonNull).collect(Collectors.toList()));
        return this;
    }

    public HttpServerRouter registerEventBusSocket(WebSocketServerEventMetadata... eventBusSocket) {
        webSocketEvents.addAll(Arrays.stream(eventBusSocket).filter(Objects::nonNull).collect(Collectors.toList()));
        return this;
    }

}
