package io.github.zero88.qwe.http.client;

import io.github.zero88.qwe.event.EventMessage;
import io.github.zero88.qwe.event.EventbusClient;
import io.github.zero88.qwe.http.HostInfo;
import io.github.zero88.qwe.http.event.WebSocketClientEventMetadata;
import io.reactivex.Single;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;

import lombok.NonNull;

/**
 * Due cache mechanism, before closing {@code Vertx}, it is mandatory to call {@link HttpClientRegistry#clear()}
 */
public interface WebSocketClientDelegate extends IClientDelegate {

    /**
     * Create new {@code Websocket client} with {@code idle timeout} is {@link HttpClientConfig#WS_IDLE_TIMEOUT_SECOND}
     * seconds
     *
     * @param vertx  Vertx
     * @param config HTTP Client config
     * @return {@code Websocket client delegate}
     */
    static WebSocketClientDelegate create(@NonNull Vertx vertx, @NonNull HttpClientConfig config) {
        return create(vertx, config, HttpClientConfig.WS_IDLE_TIMEOUT_SECOND);
    }

    /**
     * Create new {@code Websocket client} with {@code idle timeout} is {@link HttpClientConfig#WS_IDLE_TIMEOUT_SECOND}
     * seconds
     *
     * @param vertx    Vertx
     * @param config   HTTP Client config
     * @param hostInfo Override {@code host}, {@code port}, {@code SSL} option in config
     * @return {@code Websocket client delegate}
     */
    static WebSocketClientDelegate create(@NonNull Vertx vertx, @NonNull HttpClientConfig config, HostInfo hostInfo) {
        return create(vertx, config, hostInfo, HttpClientConfig.WS_IDLE_TIMEOUT_SECOND);
    }

    /**
     * Create new {@code Websocket client} with custom {@code idle timeout}
     *
     * @param vertx       Vertx
     * @param config      HTTP Client config
     * @param idleTimeout Idle timeout
     * @return {@code Websocket client delegate}
     */
    static WebSocketClientDelegate create(@NonNull Vertx vertx, @NonNull HttpClientConfig config, int idleTimeout) {
        return create(vertx, config, null, idleTimeout);
    }

    /**
     * Create new {@code Websocket client} with custom {@code idle timeout}
     *
     * @param vertx       Vertx
     * @param config      HTTP Client config
     * @param hostInfo    Override {@code host}, {@code port}, {@code SSL} option in config
     * @param idleTimeout Idle timeout
     * @return {@code Websocket client delegate}
     */
    static WebSocketClientDelegate create(@NonNull Vertx vertx, @NonNull HttpClientConfig config, HostInfo hostInfo,
                                          int idleTimeout) {
        HttpClientConfig cfg = ClientDelegate.cloneConfig(config, hostInfo, idleTimeout);
        return HttpClientRegistry.getInstance()
                                 .getWebSocket(cfg.getHostInfo(), () -> new WebSocketClientDelegateImpl(vertx, cfg));
    }

    /**
     * Blocking open websocket connection
     *
     * @param metadata Websocket metadata for {@code listener} and {@code publisher}
     * @return eventMessage for websocket status
     */
    default Single<EventMessage> open(@NonNull WebSocketClientEventMetadata metadata) {
        return open(metadata, null);
    };

    /**
     * Blocking open websocket connection
     *
     * @param metadata Websocket metadata for {@code listener} and {@code publisher}
     * @param headers  Websocket headers
     * @return eventMessage for websocket status
     */
    Single<EventMessage> open(@NonNull WebSocketClientEventMetadata metadata, MultiMap headers);

    EventbusClient getEventbus();

}
