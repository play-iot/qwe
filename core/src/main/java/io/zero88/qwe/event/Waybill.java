package io.zero88.qwe.event;

import java.util.Optional;

import io.zero88.qwe.dto.JsonData;
import io.zero88.qwe.dto.msg.RequestData;
import io.vertx.core.json.JsonObject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

/**
 * Represents for {@code bill of lading} that giving details and instructions relating to the shipment of a consignment
 * of {@code event message}.
 *
 * @see EventMessage
 * @see EventPattern
 */
@Getter
@Jacksonized
@Builder(builderClassName = "Builder")
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(doNotUseGetters = true, onlyExplicitlyIncluded = true)
public final class Waybill implements JsonData {

    /**
     * Notify address
     */
    @NonNull
    @EqualsAndHashCode.Include
    @ToString.Include
    private final String address;
    @NonNull
    @EqualsAndHashCode.Include
    @ToString.Include
    private final EventAction action;
    @Default
    @EqualsAndHashCode.Include
    @ToString.Include
    private final EventPattern pattern = EventPattern.REQUEST_RESPONSE;
    private final JsonObject payload;

    public static Waybill from(JsonObject data) {
        return Optional.ofNullable(data).map(d -> JsonData.convert(d, Waybill.class)).orElse(null);
    }

    public static Waybill from(@NonNull EventModel model, @NonNull EventAction action) {
        if (!model.getEvents().contains(action)) {
            throw new IllegalArgumentException("Action must match one of EventModel Actions");
        }
        return Waybill.builder().address(model.getAddress()).pattern(model.getPattern()).action(action).build();
    }

    public static Waybill from(@NonNull EventModel model, @NonNull EventAction action, @NonNull RequestData payload) {
        return from(model, action, payload.toJson());
    }

    public static Waybill from(@NonNull EventModel model, JsonObject payload) {
        return from(model, model.getEvents()
                                .stream()
                                .findFirst()
                                .orElseThrow(() -> new IllegalArgumentException("Not found any action")), payload);
    }

    public static Waybill from(@NonNull EventModel model, @NonNull EventAction action, JsonObject payload) {
        if (!model.getEvents().contains(action)) {
            throw new IllegalArgumentException("Action must match one of EventModel Actions");
        }
        return from(model.getAddress(), model.getPattern(), action, payload);
    }

    public static Waybill from(@NonNull String address, @NonNull EventPattern pattern, EventAction action,
                               JsonObject payload) {
        return Waybill.builder().address(address).pattern(pattern).action(action).payload(payload).build();
    }

    @JsonIgnore
    public EventMessage toMessage() {
        return EventMessage.initial(action, payload);
    }

    public static class Builder {

        @JsonProperty("payload")
        public Builder payload(@NonNull JsonObject payload) {
            this.payload = payload;
            return this;
        }

        public Builder payload(@NonNull RequestData payload) {
            return payload(payload.toJson());
        }

        public Builder payload(@NonNull JsonData payload) {
            return payload(payload.toJson());
        }

    }

}
