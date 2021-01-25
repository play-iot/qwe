package io.github.zero88.qwe.scheduler.job;

import java.util.Objects;
import java.util.Optional;

import org.quartz.JobKey;

import io.github.zero88.exceptions.ErrorCode;
import io.github.zero88.qwe.dto.JsonData;
import io.github.zero88.qwe.event.DeliveryEvent;
import io.github.zero88.qwe.event.EventPattern;
import io.github.zero88.qwe.exceptions.CarlException;
import io.github.zero88.utils.Strings;
import io.vertx.core.json.JsonObject;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.github.zero88.qwe.scheduler.job.JobModel.AbstractJobModel;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.Getter;

@Getter
@Builder(builderClassName = "Builder")
@JsonDeserialize(builder = EventJobModel.Builder.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public final class EventJobModel extends AbstractJobModel {

    @Include
    private final DeliveryEvent process;
    @Include
    private final DeliveryEvent callback;

    private EventJobModel(JobKey key, DeliveryEvent process, DeliveryEvent callback, boolean forwardIfFailure) {
        super(key, JobType.EVENT_JOB, forwardIfFailure);
        this.process = Objects.requireNonNull(process, "Job detail cannot be null");
        this.callback = callback;
    }

    @Override
    public Class<EventJob> implementation() { return EventJob.class; }

    @Override
    public JsonObject toDetail() {
        return new JsonObject().put("process", process.toJson())
                               .put("callback", Optional.ofNullable(callback).map(JsonData::toJson).orElse(null));
    }

    @Override
    public String toString() {
        return Strings.format("Type: \"{0}\" - Process Address: \"{1}\" - Callback Address: \"{2}\"", type(),
                              process.getAddress(), Objects.isNull(callback) ? "" : callback.getAddress());
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder extends AbstractJobModelBuilder<EventJobModel, Builder> {

        public EventJobModel build() {
            if (Objects.nonNull(callback) && callback.getPattern() == EventPattern.REQUEST_RESPONSE) {
                throw new CarlException(ErrorCode.INVALID_ARGUMENT,
                                        "Callback Pattern doesn't support " + EventPattern.REQUEST_RESPONSE);
            }
            return new EventJobModel(key(), process, callback, isForwardIfFailure());
        }

    }

}
