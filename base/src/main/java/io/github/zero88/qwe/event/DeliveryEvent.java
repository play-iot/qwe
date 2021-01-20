package io.github.zero88.qwe.event;

import java.util.Optional;

import io.github.zero88.qwe.dto.JsonData;
import io.github.zero88.qwe.dto.msg.RequestData;
import io.vertx.core.json.JsonObject;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * It bundles all information for single event to delivery
 */
@EqualsAndHashCode(doNotUseGetters = true, onlyExplicitlyIncluded = true)
@Builder(builderClassName = "Builder")
@ToString(onlyExplicitlyIncluded = true)
@JsonDeserialize(builder = DeliveryEvent.Builder.class)
public final class DeliveryEvent implements JsonData {

    @Getter
    @NonNull
    @EqualsAndHashCode.Include
    @ToString.Include
    private final String address;
    @Getter
    @Default
    @EqualsAndHashCode.Include
    @ToString.Include
    private final EventPattern pattern = EventPattern.REQUEST_RESPONSE;
    @Getter
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

    public EventMessage payload() {
        return EventMessage.initial(action, payload);
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {

        public Builder addPayload(@NonNull RequestData payload) {
            return payload(payload.toJson());
        }

    }

}
