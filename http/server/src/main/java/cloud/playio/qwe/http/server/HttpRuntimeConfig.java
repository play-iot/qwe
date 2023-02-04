package cloud.playio.qwe.http.server;

import java.util.Set;

import cloud.playio.qwe.http.server.gateway.GatewayApi;
import cloud.playio.qwe.http.server.rest.api.RestApi;
import cloud.playio.qwe.http.server.rest.api.RestEventApi;
import cloud.playio.qwe.http.server.ws.WebSocketServerPlan;

public interface HttpRuntimeConfig {

    Set<Class<? extends RestApi>> getRestApiClasses();

    Set<Class<? extends RestEventApi>> getRestEventApiClasses();

    Set<Class<? extends GatewayApi>> getGatewayApiClasses();

    Set<WebSocketServerPlan> getWebSocketEvents();

}
