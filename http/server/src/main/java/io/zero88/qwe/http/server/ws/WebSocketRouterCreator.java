package io.zero88.qwe.http.server.ws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.github.zero88.repl.Arguments;
import io.github.zero88.repl.ReflectionClass;
import io.github.zero88.utils.Strings;
import io.github.zero88.utils.Urls;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.http.server.BasePaths;
import io.zero88.qwe.http.server.HttpSystem.WebSocketSystem;
import io.zero88.qwe.http.server.RouterCreator;
import io.zero88.qwe.http.server.config.WebSocketConfig;
import io.zero88.qwe.http.server.config.WebSocketConfig.SocketBridgeConfig;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

public final class WebSocketRouterCreator implements RouterCreator<WebSocketConfig>, WebSocketSystem {

    @Getter(AccessLevel.MODULE)
    private final Map<String, List<WebSocketServerPlan>> socketsByPath = new HashMap<>();

    public WebSocketRouterCreator(Collection<WebSocketServerPlan> metadataSockets) {
        metadataSockets.stream().filter(Objects::nonNull).forEach(this::register);
    }

    @Override
    public boolean validate(WebSocketConfig config) {
        return !this.socketsByPath.isEmpty();
    }

    @Override
    public Router subRouter(@NonNull WebSocketConfig config, @NonNull SharedDataLocalProxy sharedData) {
        final SockJSHandler sockJSHandler = SockJSHandler.create(sharedData.getVertx(), config.getSockjsOptions());
        final Router router = Router.router(sharedData.getVertx());
        socketsByPath.forEach((path, mapping) -> router.mountSubRouter(path, sockJSHandler.bridge(
            createBridgeOptions(config.getBridgeOptions(), mapping,
                                BasePaths.addWildcards(Urls.combinePath(config.getPath(), path))),
            createHandler(sharedData, mapping, config.bridgeHandlerClass()))));
        return router;
    }

    private void register(@NonNull WebSocketServerPlan socketMetadata) {
        socketsByPath.computeIfAbsent(socketMetadata.getPath(), k -> new ArrayList<>()).add(socketMetadata);
    }

    private SockJSBridgeOptions createBridgeOptions(SocketBridgeConfig config, List<WebSocketServerPlan> metadata,
                                                    String fullPath) {
        SockJSBridgeOptions opts = new SockJSBridgeOptions(config);
        metadata.forEach(m -> {
            if (!m.isOnlyOutbound()) {
                logger().info(decor("Add Inbound Permitted [{}][{}=>{}]"), fullPath, m.inboundAddress(),
                              m.processAddress());
                opts.addInboundPermitted(new PermittedOptions().setAddress(m.inboundAddress()));
            }
            if (Strings.isNotBlank(m.outboundAddress())) {
                logger().info(decor("Add Outbound Permitted [{}][{}][{}]"), fullPath, m.outboundAddress(),
                              m.outbound().getPattern());
                opts.addOutboundPermitted(new PermittedOptions().setAddress(m.outboundAddress()));
            }
        });
        return opts;
    }

    private WebSocketBridgeEventHandler createHandler(@NonNull SharedDataLocalProxy sharedData,
                                                      @NonNull List<WebSocketServerPlan> socketMapping,
                                                      @NonNull Class<? extends WebSocketBridgeEventHandler> clazz) {
        WebSocketBridgeEventHandler handler = ReflectionClass.createObject(clazz, new Arguments().put(
            SharedDataLocalProxy.class, sharedData).put(List.class, socketMapping));
        return Objects.isNull(handler) ? new WebSocketBridgeEventHandler(sharedData, socketMapping) : handler;
    }

}
