package io.github.zero88.qwe.scheduler.job;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.utils.Key;

import io.github.zero88.qwe.dto.JsonData;
import io.github.zero88.utils.Strings;
import io.vertx.core.json.JsonObject;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonSubTypes( {
    @JsonSubTypes.Type(value = EventJobModel.class, name = "EVENT_JOB")
})
public interface JobModel<T extends VertxJob> extends JsonData {

    String JOB_DATA_KEY = "jobModel";

    static JobKey createKey(String group, String name) {
        return new JobKey(Strings.isBlank(name) ? Key.createUniqueName(group) : name, group);
    }

    @JsonUnwrapped
    JobKey getKey();

    @JsonProperty(value = "type", required = true)
    @JsonUnwrapped
    JobType type();

    Class<T> implementation();

    @JsonProperty(value = "forwardIfFailure")
    boolean forwardIfFailure();

    JsonObject toDetail();

    String toString();

    default JobDetail toJobDetail() {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(JOB_DATA_KEY, this);
        return JobBuilder.newJob(implementation()).withIdentity(getKey()).setJobData(jobDataMap).build();
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    abstract class AbstractJobModel implements JobModel {

        @Getter
        @Include
        private final JobKey key;
        @Include
        private final JobType type;
        @Include
        private final boolean forwardIfFailure;

        @Override
        public final JobType type() { return type; }

        @Override
        public boolean forwardIfFailure() { return forwardIfFailure; }

        @SuppressWarnings("unchecked")
        public static abstract class AbstractJobModelBuilder<T extends JobModel, B extends AbstractJobModelBuilder> {

            @Getter
            boolean forwardIfFailure = true;
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

            public B forwardIfFailure(boolean forwardIfFailure) {
                this.forwardIfFailure = forwardIfFailure;
                return (B) this;
            }

            protected JobKey key() { return createKey(group, name); }

            public abstract T build();

        }

    }

}
