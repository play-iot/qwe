package io.zero88.qwe.http.server.ws;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.github.zero88.utils.Functions;
import io.github.zero88.utils.Strings;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.bridge.BridgeEventType;
import io.vertx.ext.web.handler.sockjs.BridgeEvent;
import io.vertx.ext.web.handler.sockjs.SockJSSocket;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.dto.JsonData;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.event.EventMessage;
import io.zero88.qwe.event.bridge.EventBridgeExecutor;
import io.zero88.qwe.event.bridge.EventBridgePlan;
import io.zero88.qwe.exceptions.QWEException;
import io.zero88.qwe.http.HttpUtils.HttpHeaderUtils;
import io.zero88.qwe.http.server.HttpSystem.WebSocketSystem;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * WebSocket bridge event bus handler
 */
//TODO handle auth with socket header
@Slf4j
public class WebSocketBridgeEventHandler implements Handler<BridgeEvent>, WebSocketSystem {

    private final EventBridgeExecutor executor;
    private final Map<String, WebSocketServerPlan> c2sAddresses;
    private final Map<String, WebSocketServerPlan> s2cAddresses;

    public WebSocketBridgeEventHandler(@NonNull SharedDataLocalProxy sharedData,
                                       @NonNull List<WebSocketServerPlan> addressMap) {
        this.executor = () -> sharedData;
        this.c2sAddresses = addressMap.stream()
                                      .filter(p -> !p.isOnlyOutbound())
                                      .collect(Collectors.toMap(EventBridgePlan::inboundAddress, Function.identity()));
        this.s2cAddresses = addressMap.stream()
                                      .filter(p -> Strings.isNotBlank(p.outboundAddress()))
                                      .collect(Collectors.toMap(EventBridgePlan::outboundAddress, Function.identity()));
    }

    @Override
    public void handle(BridgeEvent event) {
        if (event.type() == BridgeEventType.SEND) {
            clientToServer(event, logEvent(event, false));
        } else if (event.type() == BridgeEventType.RECEIVE) {
            serverToClient(event, logEvent(event, false));
        } else {
            logEvent(event, true);
        }
        event.tryComplete(true);
    }

    private SockJSSocket logEvent(BridgeEvent event, boolean debug) {
        final SockJSSocket socket = event.socket();
        final String msg = decor("RemoteAddr[{}] - SocketAddr[{}] - Path[{}] - Event[{}]");
        if (debug) {
            log.debug(msg, socket.remoteAddress(), socket.localAddress(), socket.uri(), event.type());
        } else {
            log.info(msg, socket.remoteAddress(), socket.localAddress(), socket.uri(), event.type());
        }
        return socket;
    }

    private void clientToServer(BridgeEvent event, SockJSSocket socket) {
        final JsonObject raw = event.getRawMessage();
        final String address = raw.getString("address");
        final WebSocketServerPlan plan = c2sAddresses.get(address);
        if (Objects.isNull(plan)) {
            //            handleMessage(socket).accept(EventMessage.error(EventAction.ACK, ErrorCode.SERVICE_NOT_FOUND,
            //                                                            "Not found bridge to address [" + address +
            //                                                           "]"));
            return;
        }
        try {
            EventMessage msg = parseMessage(HttpHeaderUtils.serializeHeaders(socket.headers()), raw);
            log.info(decor("Forward the bridge message [{}=>{}][{}]"), address, plan.processAddress(), msg.getAction());
            executor.execute(plan, msg, handleMessage(socket));
        } catch (QWEException e) {
            handleMessage(socket).accept(EventMessage.error(EventAction.ACK, e));
        }
    }

    private void serverToClient(BridgeEvent event, SockJSSocket socket) {
        final JsonObject raw = event.getRawMessage();
        final String address = raw.getString("address");
        final WebSocketServerPlan plan = s2cAddresses.get(address);
        if (Objects.isNull(plan)) {
            //            event.setRawMessage(EventMessage.error(EventAction.ACK, ErrorCode.SERVICE_NOT_FOUND,
            //                                                   "Not found bridge to address [" + address + "]")
            //                                                  .toJson());
            return;
        }
        try {
            EventMessage msg = JsonData.from(raw.getJsonObject("body"), EventMessage.class,
                                             "Invalid WebSocket message");
            log.info(decor("Publish the bridge message on [{}][{}=>{}]"), address, msg.getAction(),
                     msg.getPrevAction());
            event.setRawMessage(msg.toJson());
        } catch (QWEException e) {
            event.setRawMessage(EventMessage.error(EventAction.ACK, e).toJson());
        }
    }

    //TODO May override consumer for message
    protected Consumer<EventMessage> handleMessage(SockJSSocket socket) {
        return message -> socket.write(message.toJson().toBuffer());
    }

    private EventMessage parseMessage(JsonObject headers, JsonObject raw) {
        final EventMessage body = JsonData.from(raw.getJsonObject("body"), EventMessage.class,
                                                "Invalid WebSocket message");
        return EventMessage.initial(body.getAction(), Functions.getOrDefault(() -> {
            final RequestData req = JsonData.from(body.getData(), RequestData.class);
            return req.setHeaders(headers.mergeIn(req.headers()));
        }, () -> RequestData.builder().body(body.getData()).headers(headers).build()));
    }

}
