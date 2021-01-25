package io.github.zero88.qwe.scheduler.job;

import io.github.zero88.qwe.dto.EnumType;
import io.github.zero88.qwe.dto.EnumType.AbstractEnumType;

import com.fasterxml.jackson.annotation.JsonCreator;

public final class JobType extends AbstractEnumType {

    public static final JobType EVENT_JOB = new JobType("EVENT_JOB");

    private JobType(String type) {
        super(type);
    }

    @JsonCreator
    public static JobType factory(String type) {
        return EnumType.factory(type, JobType.class, true);
    }

}
