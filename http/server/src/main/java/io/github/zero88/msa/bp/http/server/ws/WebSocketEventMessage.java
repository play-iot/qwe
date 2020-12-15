package io.github.zero88.msa.bp.http.server.ws;

import java.io.Serializable;
import java.util.Objects;

import io.github.zero88.msa.bp.dto.JsonData;
import io.github.zero88.msa.bp.event.EventMessage;
import io.github.zero88.msa.bp.exceptions.InitializerError;
import io.github.zero88.utils.Strings;
import io.vertx.ext.bridge.BridgeEventType;

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

    @JsonCreator
    private WebSocketEventMessage(@JsonProperty(value = "address", required = true) String address,
                                  @JsonProperty(value = "type", required = true) BridgeEventType type,
                                  @JsonProperty(value = "body") EventMessage body) {
        try {
            this.address = Strings.requireNotBlank(address);
            this.type = Objects.requireNonNull(type);
            this.body = body;
        } catch (NullPointerException | IllegalArgumentException e) {
            throw new InitializerError("Create " + WebSocketEventMessage.class.getSimpleName() + " object failure", e);
        }
    }

    public static WebSocketEventMessage from(Object object) {
        return JsonData.from(object, WebSocketEventMessage.class, "Invalid websocket event body format");
    }

}
