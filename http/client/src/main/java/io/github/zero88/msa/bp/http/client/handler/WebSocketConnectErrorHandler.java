package io.github.zero88.msa.bp.http.client.handler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import io.github.zero88.msa.bp.event.EventAction;
import io.github.zero88.msa.bp.event.EventMessage;
import io.github.zero88.msa.bp.event.EventbusClient;
import io.github.zero88.msa.bp.exceptions.CarlException;
import io.github.zero88.msa.bp.exceptions.HttpException;
import io.github.zero88.msa.bp.exceptions.TimeoutException;
import io.github.zero88.msa.bp.http.HostInfo;
import io.github.zero88.msa.bp.http.HttpStatusMapping;
import io.github.zero88.msa.bp.http.client.HttpClientRegistry;
import io.github.zero88.utils.Reflections.ReflectionClass;
import io.reactivex.functions.Function;
import io.vertx.core.VertxException;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.UpgradeRejectedException;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class WebSocketConnectErrorHandler implements Function<Throwable, EventMessage> {

    private final HostInfo hostInfo;
    private final EventbusClient eventbus;

    @SuppressWarnings("unchecked")
    public static <T extends WebSocketConnectErrorHandler> T create(@NonNull HostInfo hostInfo,
                                                                    @NonNull EventbusClient controller,
                                                                    @NonNull Class<T> connErrorHandlerClass) {
        if (Objects.isNull(connErrorHandlerClass) || WebSocketConnectErrorHandler.class.equals(connErrorHandlerClass)) {
            return (T) new WebSocketConnectErrorHandler(hostInfo, controller) {};
        }
        Map<Class, Object> params = new LinkedHashMap<>();
        params.put(HostInfo.class, hostInfo);
        params.put(EventbusClient.class, controller);
        return ReflectionClass.createObject(connErrorHandlerClass, params);
    }

    @Override
    public EventMessage apply(Throwable error) {
        HttpClientRegistry.getInstance().remove(hostInfo, true);
        return EventMessage.error(EventAction.parse("OPEN"), convert(error));
    }

    private Throwable convert(Throwable error) {
        if (error instanceof VertxException && error.getMessage().equals("Connection was closed")) {
            return new TimeoutException("Request timeout", error);
        }
        if (error instanceof UpgradeRejectedException) {
            final int status = ((UpgradeRejectedException) error).getStatus();
            return new HttpException(status, error.getMessage(),
                                     new CarlException(HttpStatusMapping.error(HttpMethod.GET, status), error));
        }
        return new HttpException("Failed when open WebSocket connection", error);
    }

}
