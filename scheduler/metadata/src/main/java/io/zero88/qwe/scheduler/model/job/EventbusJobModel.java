package io.zero88.qwe.scheduler.model.job;

import java.util.Objects;

import org.quartz.JobKey;

import io.github.zero88.exceptions.ErrorCode;
import io.zero88.qwe.event.Waybill;
import io.zero88.qwe.event.EventPattern;
import io.zero88.qwe.exceptions.CarlException;
import io.github.zero88.utils.Strings;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;
import lombok.extern.jackson.Jacksonized;

/**
 * Represents for eventbus job model
 *
 * @see QWEJobModel
 * @see Waybill
 */
@Getter
@Jacksonized
@FieldNameConstants
@Builder(builderClassName = "Builder")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public final class EventbusJobModel extends AbstractQWEJobModel {

    public static final JobType EVENTBUS_JOB = JobType.factory("EVENTBUS_JOB");
    /**
     * Defines an input information to execute job
     */
    @Include
    private final Waybill process;
    /**
     * Defines an callback information to share result after finish job
     */
    @Include
    private final Waybill callback;

    private EventbusJobModel(JobKey key, Waybill process, Waybill callback, boolean forwardIfFailure) {
        super(key, forwardIfFailure);
        this.process = Objects.requireNonNull(process, "Job detail cannot be null");
        this.callback = callback;
    }

    @Override
    public JobType type() {
        return EVENTBUS_JOB;
    }

    @Override
    public String toString() {
        return Strings.format("Type: \"{0}\" - Process Address: \"{1}\" - Callback Address: \"{2}\"", type(),
                              process.getAddress(), Objects.isNull(callback) ? "" : callback.getAddress());
    }

    public static class Builder extends AbstractJobModelBuilder<EventbusJobModel, Builder> {

        public EventbusJobModel build() {
            if (Objects.nonNull(callback) && callback.getPattern() == EventPattern.REQUEST_RESPONSE) {
                throw new CarlException(ErrorCode.INVALID_ARGUMENT,
                                        "Callback Pattern doesn't support " + EventPattern.REQUEST_RESPONSE);
            }
            return new EventbusJobModel(key(), process, callback, isForwardIfFailure());
        }

    }

}
