package io.github.zero88.qwe.http.server.ws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.github.zero88.exceptions.InvalidUrlException;
import io.github.zero88.qwe.component.SharedDataLocalProxy;
import io.github.zero88.qwe.event.EventModel;
import io.github.zero88.qwe.exceptions.InitializerError;
import io.github.zero88.qwe.http.event.WebSocketServerEventMetadata;
import io.github.zero88.qwe.http.server.BasePaths;
import io.github.zero88.qwe.http.server.HttpConfig.WebSocketConfig;
import io.github.zero88.qwe.http.server.HttpConfig.WebSocketConfig.SocketBridgeConfig;
import io.github.zero88.qwe.http.server.RouterCreator;
import io.github.zero88.utils.Reflections.ReflectionClass;
import io.github.zero88.utils.Urls;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class WebSocketRouterCreator implements RouterCreator<WebSocketConfig> {

    private final Map<String, List<WebSocketServerEventMetadata>> socketsByPath = new HashMap<>();

    public WebSocketRouterCreator(Collection<WebSocketServerEventMetadata> metadataSockets) {
        metadataSockets.stream().filter(Objects::nonNull).forEach(this::register);
    }

    private void register(@NonNull WebSocketServerEventMetadata socketMetadata) {
        socketsByPath.computeIfAbsent(socketMetadata.getPath(), k -> new ArrayList<>()).add(socketMetadata);
    }

    @Override
    public @NonNull String mountPoint(@NonNull WebSocketConfig config) {
        String root = Urls.combinePath(config.basePath());
        if (!Urls.validatePath(root)) {
            throw new InvalidUrlException("Root Websocket is not valid");
        }
        return root;
    }

    @Override
    public Router router(@NonNull WebSocketConfig config, @NonNull SharedDataLocalProxy sharedData) {
        final SockJSHandler sockJSHandler = SockJSHandler.create(sharedData.getVertx(), config.getSockjsOptions());
        final Router router = Router.router(sharedData.getVertx());
        validate().forEach((path, socketMapping) -> {
            final SockJSBridgeOptions options = createBridgeOptions(config.getBridgeOptions(), socketMapping,
                                                                    BasePaths.addWildcards("/"));
            final WebSocketBridgeEventHandler handler = createHandler(sharedData, socketMapping,
                                                                      config.bridgeHandlerClass());
            router.mountSubRouter(path, sockJSHandler.bridge(options, handler));
        });
        return router;
    }

    Map<String, List<WebSocketServerEventMetadata>> validate() {
        if (this.socketsByPath.isEmpty()) {
            throw new InitializerError("No socket handler given, register at least one.");
        }
        return socketsByPath;
    }

    private SockJSBridgeOptions createBridgeOptions(SocketBridgeConfig config,
                                                    List<WebSocketServerEventMetadata> metadata, String fullPath) {
        SockJSBridgeOptions opts = new SockJSBridgeOptions(config);
        metadata.forEach(m -> {
            EventModel listener = m.getListener();
            EventModel publisher = m.getPublisher();
            if (Objects.nonNull(listener)) {
                log.info("Registering websocket | Event Listener :\t{} --- {} {}", fullPath, listener.getPattern(),
                         listener.getAddress());
                opts.addInboundPermitted(new PermittedOptions().setAddress(listener.getAddress()));
            } else if (Objects.nonNull(publisher)) {
                log.info("Registering websocket | Event Publisher:\t{} --- {} {}", fullPath, publisher.getPattern(),
                         publisher.getAddress());
                opts.addOutboundPermitted(new PermittedOptions().setAddress(publisher.getAddress()));
            }
        });
        return opts;
    }

    private WebSocketBridgeEventHandler createHandler(@NonNull SharedDataLocalProxy sharedData,
                                                      @NonNull List<WebSocketServerEventMetadata> socketMapping,
                                                      @NonNull Class<? extends WebSocketBridgeEventHandler> clazz) {
        Map<Class, Object> map = new LinkedHashMap<>();
        map.put(SharedDataLocalProxy.class, sharedData);
        map.put(List.class, socketMapping);
        WebSocketBridgeEventHandler handler = ReflectionClass.createObject(clazz, map);
        return Objects.isNull(handler) ? new WebSocketBridgeEventHandler(sharedData, socketMapping) : handler;
    }

}
