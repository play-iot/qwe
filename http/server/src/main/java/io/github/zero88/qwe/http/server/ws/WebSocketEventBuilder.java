package io.github.zero88.qwe.http.server.ws;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.zero88.exceptions.InvalidUrlException;
import io.github.zero88.qwe.component.SharedDataDelegate;
import io.github.zero88.qwe.event.EventModel;
import io.github.zero88.qwe.event.EventbusClient;
import io.github.zero88.qwe.exceptions.InitializerError;
import io.github.zero88.qwe.http.event.WebSocketServerEventMetadata;
import io.github.zero88.qwe.http.server.ApiConstants;
import io.github.zero88.qwe.http.server.HttpConfig.WebSocketConfig;
import io.github.zero88.qwe.http.server.handler.WebSocketBridgeEventHandler;
import io.github.zero88.utils.Reflections.ReflectionClass;
import io.github.zero88.utils.Strings;
import io.github.zero88.utils.Urls;
import io.vertx.core.Vertx;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class WebSocketEventBuilder {

    private final Logger logger = LoggerFactory.getLogger(WebSocketEventBuilder.class);
    private final Vertx vertx;
    private final Router router;
    private final String sharedKey;
    private final Map<String, List<WebSocketServerEventMetadata>> socketsByPath = new HashMap<>();
    private WebSocketConfig webSocketConfig;
    private Class<? extends WebSocketBridgeEventHandler> bridgeHandlerClass = WebSocketBridgeEventHandler.class;
    @Getter(AccessLevel.PACKAGE)
    private String rootWs = ApiConstants.ROOT_WS_PATH;

    /**
     * For test
     */
    WebSocketEventBuilder() {
        this(null, null, WebSocketEventBuilder.class.getName());
    }

    public WebSocketEventBuilder rootWs(String rootWs) {
        if (Strings.isNotBlank(rootWs)) {
            String root = Urls.combinePath(rootWs);
            if (!Urls.validatePath(root)) {
                throw new InvalidUrlException("Root Websocket is not valid");
            }
            this.rootWs = root;
        }
        return this;
    }

    public WebSocketEventBuilder register(@NonNull WebSocketServerEventMetadata socketMetadata) {
        socketsByPath.computeIfAbsent(socketMetadata.getPath(), k -> new ArrayList<>()).add(socketMetadata);
        return this;
    }

    public WebSocketEventBuilder register(@NonNull WebSocketServerEventMetadata... eventBusSockets) {
        return this.register(Arrays.asList(eventBusSockets));
    }

    public WebSocketEventBuilder register(@NonNull Collection<WebSocketServerEventMetadata> eventBusSockets) {
        eventBusSockets.stream().filter(Objects::nonNull).forEach(this::register);
        return this;
    }

    public WebSocketEventBuilder handler(@NonNull Class<? extends WebSocketBridgeEventHandler> handler) {
        this.bridgeHandlerClass = handler;
        return this;
    }

    public WebSocketEventBuilder options(@NonNull WebSocketConfig websocketConfig) {
        this.webSocketConfig = websocketConfig;
        return this;
    }

    public Router build() {
        SockJSHandler sockJSHandler = SockJSHandler.create(vertx, config().getSockjsOptions());
        EventbusClient controller = SharedDataDelegate.getEventController(vertx, sharedKey);
        validate().forEach((path, socketMapping) -> {
            String fullPath = Urls.combinePath(rootWs, path, ApiConstants.WILDCARDS_ANY_PATH);
            sockJSHandler.bridge(createBridgeOptions(fullPath, socketMapping),
                                 createHandler(controller, socketMapping));
            //FIXME websocket handler??
            router.route(fullPath).handler(sockJSHandler);
        });
        return router;
    }

    Map<String, List<WebSocketServerEventMetadata>> validate() {
        if (this.socketsByPath.isEmpty()) {
            throw new InitializerError("No socket handler given, register at least one.");
        }
        return socketsByPath;
    }

    private SockJSBridgeOptions createBridgeOptions(String fullPath, List<WebSocketServerEventMetadata> metadata) {
        SockJSBridgeOptions opts = new SockJSBridgeOptions(config().getBridgeOptions());
        metadata.forEach(m -> {
            EventModel listener = m.getListener();
            EventModel publisher = m.getPublisher();
            if (Objects.nonNull(listener)) {
                logger.info("Registering websocket | Event Listener :\t{} --- {} {}", fullPath, listener.getPattern(),
                            listener.getAddress());
                opts.addInboundPermitted(new PermittedOptions().setAddress(listener.getAddress()));
            } else if (Objects.nonNull(publisher)) {
                logger.info("Registering websocket | Event Publisher:\t{} --- {} {}", fullPath, publisher.getPattern(),
                            publisher.getAddress());
                opts.addOutboundPermitted(new PermittedOptions().setAddress(publisher.getAddress()));
            }
        });
        return opts;
    }

    private WebSocketBridgeEventHandler createHandler(EventbusClient controller,
                                                      List<WebSocketServerEventMetadata> socketMapping) {
        Map<Class, Object> map = new LinkedHashMap<>();
        map.put(EventbusClient.class, controller);
        map.put(List.class, socketMapping);
        WebSocketBridgeEventHandler handler = ReflectionClass.createObject(bridgeHandlerClass, map);
        return Objects.isNull(handler) ? new WebSocketBridgeEventHandler(controller, socketMapping) : handler;
    }

    private WebSocketConfig config() {
        return Objects.requireNonNull(webSocketConfig);
    }

}
