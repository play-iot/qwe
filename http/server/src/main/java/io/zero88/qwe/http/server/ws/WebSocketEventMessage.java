package io.zero88.qwe.http.server.ws;

import java.io.Serializable;
import java.util.Objects;

import io.github.zero88.utils.Functions;
import io.github.zero88.utils.Strings;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.bridge.BridgeEventType;
import io.zero88.qwe.dto.JsonData;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.event.EventMessage;
import io.zero88.qwe.exceptions.InitializerError;
import io.zero88.qwe.http.server.ws.jackson.BridgeEventTypeDeserialize;
import io.zero88.qwe.http.server.ws.jackson.BridgeEventTypeSerialize;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder(builderClassName = "Builder")
public class WebSocketEventMessage implements Serializable, JsonData {

    @EqualsAndHashCode.Include
    private final String address;
    @JsonDeserialize(using = BridgeEventTypeDeserialize.class)
    @JsonSerialize(using = BridgeEventTypeSerialize.class)
    private final BridgeEventType type;
    private final EventMessage body;
    private final JsonObject headers;

    @JsonCreator
    private WebSocketEventMessage(@JsonProperty(value = "address", required = true) String address,
                                  @JsonProperty(value = "type", required = true) BridgeEventType type,
                                  @JsonProperty(value = "body") EventMessage body,
                                  @JsonProperty(value = "headers") JsonObject headers) {
        try {
            this.address = Strings.requireNotBlank(address);
            this.type = Objects.requireNonNull(type);
            this.body = body;
            this.headers = headers;
        } catch (NullPointerException | IllegalArgumentException e) {
            throw new InitializerError("Create " + WebSocketEventMessage.class.getSimpleName() + " object failure", e);
        }
    }

    public EventMessage toEventMessage() {
        return EventMessage.initial(body.getAction(),
                                    Functions.getOrDefault(() -> JsonData.from(body.getData(), RequestData.class),
                                                           () -> RequestData.builder()
                                                                            .body(body.getData())
                                                                            .headers(headers)
                                                                            .build()));
    }

    public static WebSocketEventMessage from(Object msg) {
        return JsonData.from(msg, WebSocketEventMessage.class, "Invalid WebSocket message");
    }

}
