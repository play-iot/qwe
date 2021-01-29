package io.github.zero88.qwe.event;

import java.util.Optional;

import io.github.zero88.qwe.dto.JsonData;
import io.github.zero88.qwe.dto.msg.RequestData;
import io.vertx.core.json.JsonObject;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

/**
 * It bundles all information for single event to delivery
 */
@Getter
@Jacksonized
@Builder(builderClassName = "Builder")
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(doNotUseGetters = true, onlyExplicitlyIncluded = true)
public final class DeliveryEvent implements JsonData {

    @NonNull
    @EqualsAndHashCode.Include
    @ToString.Include
    private final String address;
    @Default
    @EqualsAndHashCode.Include
    @ToString.Include
    private final EventPattern pattern = EventPattern.REQUEST_RESPONSE;
    @NonNull
    @EqualsAndHashCode.Include
    @ToString.Include
    private final EventAction action;
    private final JsonObject payload;

    public static DeliveryEvent from(JsonObject data) {
        return Optional.ofNullable(data).map(d -> JsonData.convert(d, DeliveryEvent.class)).orElse(null);
    }

    public static DeliveryEvent from(@NonNull EventModel model, @NonNull EventAction action) {
        if (!model.getEvents().contains(action)) {
            throw new IllegalArgumentException("Action must match one of EventModel Actions");
        }
        return new DeliveryEvent(model.getAddress(), model.getPattern(), action, null);
    }

    public static DeliveryEvent from(@NonNull EventModel model, @NonNull EventAction action,
                                     @NonNull RequestData payload) {
        return from(model, action, payload.toJson());
    }

    public static DeliveryEvent from(@NonNull EventModel model, JsonObject payload) {
        return from(model, model.getEvents()
                                .stream()
                                .findFirst()
                                .orElseThrow(() -> new IllegalArgumentException("Not found any action")), payload);
    }

    public static DeliveryEvent from(@NonNull EventModel model, @NonNull EventAction action, JsonObject payload) {
        if (!model.getEvents().contains(action)) {
            throw new IllegalArgumentException("Action must match one of EventModel Actions");
        }
        return new DeliveryEvent(model.getAddress(), model.getPattern(), action, payload);
    }

    public static DeliveryEvent from(@NonNull String address, @NonNull EventPattern pattern, EventAction action,
                                     JsonObject payload) {
        return new DeliveryEvent(address, pattern, action, payload);
    }

    @JsonIgnore
    public EventMessage getPayload() {
        return EventMessage.initial(action, payload);
    }

    public static class Builder {

        public Builder addPayload(@NonNull RequestData payload) {
            return payload(payload.toJson());
        }

    }

}
