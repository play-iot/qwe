package io.zero88.qwe.eventbus;

import io.zero88.qwe.dto.JsonData;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public final class DeliveryEvent implements JsonData {

    private final String address;
    private final EventAction action;
    @Default
    private final EventPattern pattern = EventPattern.REQUEST_RESPONSE;
    @Default
    private final boolean useRequestData = true;

}
