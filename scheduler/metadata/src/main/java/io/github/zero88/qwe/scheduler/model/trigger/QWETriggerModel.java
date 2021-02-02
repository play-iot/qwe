package io.github.zero88.qwe.scheduler.model.trigger;

import org.quartz.ScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.utils.Key;

import io.github.zero88.qwe.dto.JsonData;
import io.github.zero88.qwe.scheduler.model.SchedulerModel;
import io.github.zero88.utils.Strings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Declares a {@code Trigger model} that is define 'mechanism' by which {@code job} is scheduled
 */
@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonSubTypes( {
    @JsonSubTypes.Type(value = CronTriggerModel.class, name = "CRON"),
    @JsonSubTypes.Type(value = PeriodicTriggerModel.class, name = "PERIODIC")
})
public interface QWETriggerModel extends SchedulerModel {

    static TriggerKey createKey(String group, String name) {
        return new TriggerKey(Strings.isBlank(name) ? Key.createUniqueName(group) : name, group);
    }

    static QWETriggerModel from(String group, String name, TriggerOption option) {
        return JsonData.from(option.toJson().put("group", group).put("name", name), QWETriggerModel.class,
                             JsonData.LENIENT_MAPPER);
    }

    @JsonUnwrapped
    TriggerKey getKey();

    @JsonProperty(value = "type", required = true)
    @JsonUnwrapped
    TriggerType type();

    String logicalThread();

    @RequiredArgsConstructor
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    abstract class AbstractTriggerModel implements QWETriggerModel {

        @Getter
        @EqualsAndHashCode.Include
        private final TriggerKey key;
        @EqualsAndHashCode.Include
        private final TriggerType type;

        @NonNull
        protected abstract ScheduleBuilder<? extends Trigger> scheduleBuilder();

        @Override
        public final TriggerType type() { return type; }

        @SuppressWarnings("unchecked")
        static abstract class AbstractTriggerModelBuilder<T extends QWETriggerModel,
                                                                     B extends AbstractTriggerModelBuilder> {

            String name;
            String group;

            public B group(String group) {
                this.group = group;
                return (B) this;
            }

            public B name(String name) {
                this.name = name;
                return (B) this;
            }

            protected TriggerKey key() { return createKey(group, name); }

            public abstract T build();

        }

    }

}
