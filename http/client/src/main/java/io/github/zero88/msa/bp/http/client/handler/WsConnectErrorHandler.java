package io.github.zero88.msa.bp.http.client.handler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import io.github.zero88.msa.bp.event.EventbusClient;
import io.github.zero88.msa.bp.exceptions.BlueprintException;
import io.github.zero88.msa.bp.exceptions.HttpException;
import io.github.zero88.msa.bp.exceptions.TimeoutException;
import io.github.zero88.msa.bp.http.HostInfo;
import io.github.zero88.msa.bp.http.HttpStatusMapping;
import io.github.zero88.msa.bp.http.client.HttpClientRegistry;
import io.github.zero88.utils.Reflections.ReflectionClass;
import io.vertx.core.Handler;
import io.vertx.core.VertxException;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.UpgradeRejectedException;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class WsConnectErrorHandler implements Handler<Throwable> {

    private final HostInfo hostInfo;
    private final EventbusClient controller;

    @SuppressWarnings("unchecked")
    public static <T extends WsConnectErrorHandler> T create(@NonNull HostInfo hostInfo,
                                                             @NonNull EventbusClient controller,
                                                             @NonNull Class<T> connErrorHandlerClass) {
        if (Objects.isNull(connErrorHandlerClass) || WsConnectErrorHandler.class.equals(connErrorHandlerClass)) {
            return (T) new WsConnectErrorHandler(hostInfo, controller) {};
        }
        Map<Class, Object> params = new LinkedHashMap<>();
        params.put(HostInfo.class, hostInfo);
        params.put(EventbusClient.class, controller);
        return ReflectionClass.createObject(connErrorHandlerClass, params);
    }

    @Override
    public void handle(Throwable error) {
        HttpClientRegistry.getInstance().remove(hostInfo, true);
        if (error instanceof VertxException && error.getMessage().equals("Connection was closed")) {
            throw new TimeoutException("Request timeout", error);
        }
        if (error instanceof UpgradeRejectedException) {
            final int status = ((UpgradeRejectedException) error).getStatus();
            throw new HttpException(status, error.getMessage(),
                                    new BlueprintException(HttpStatusMapping.error(HttpMethod.GET, status), error));
        }
        throw new HttpException("Failed when open WebSocket connection", error);
    }

}
