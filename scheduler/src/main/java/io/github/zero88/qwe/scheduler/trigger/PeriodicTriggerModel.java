package io.github.zero88.qwe.scheduler.trigger;

import org.quartz.ScheduleBuilder;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerKey;

import io.github.zero88.qwe.scheduler.trigger.TriggerModel.AbstractTriggerModel;
import io.vertx.core.json.JsonObject;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder(builderClassName = "Builder")
@JsonDeserialize(builder = PeriodicTriggerModel.Builder.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public final class PeriodicTriggerModel extends AbstractTriggerModel {

    /**
     * Specify a repeat interval in seconds - which will then be multiplied by 1000 to produce milliseconds.
     */
    @Include
    private final int intervalInSeconds;
    /**
     * Specify a the number of time the trigger will repeat - total number of firings will be this number + 1.
     */
    @Include
    private final int repeat;

    private PeriodicTriggerModel(TriggerType type, TriggerKey key, int intervalInSeconds, int repeat) {
        super(key, type);
        this.intervalInSeconds = intervalInSeconds;
        this.repeat = repeat;
    }

    @Override
    protected @NonNull ScheduleBuilder<SimpleTrigger> scheduleBuilder() {
        return SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(intervalInSeconds).withRepeatCount(repeat);
    }

    @Override
    public JsonObject toDetail() {
        return new JsonObject().put("intervalInSeconds", intervalInSeconds).put("repeat", repeat);
    }

    @Override
    public String logicalThread() {
        return null;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder extends AbstractTriggerModelBuilder<PeriodicTriggerModel, Builder> {

        public PeriodicTriggerModel build() {
            return new PeriodicTriggerModel(TriggerType.PERIODIC, key(), intervalInSeconds,
                                            repeat < 0 || repeat >= Integer.MAX_VALUE
                                            ? SimpleTrigger.REPEAT_INDEFINITELY
                                            : repeat);
        }

    }

}
