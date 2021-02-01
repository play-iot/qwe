package io.github.zero88.qwe.scheduler.service;

import io.github.zero88.qwe.dto.JsonData;
import io.github.zero88.qwe.scheduler.model.job.QWEJobModel;
import io.github.zero88.qwe.scheduler.model.trigger.TriggerModel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.extern.jackson.Jacksonized;

@SuppressWarnings("rawtypes")
@Getter
@FieldNameConstants
@Jacksonized
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class SchedulerArgs implements JsonData {

    @Include
    @JsonProperty(Fields.job)
    private final QWEJobModel job;
    @Include
    @JsonProperty(Fields.trigger)
    private final TriggerModel trigger;

}
