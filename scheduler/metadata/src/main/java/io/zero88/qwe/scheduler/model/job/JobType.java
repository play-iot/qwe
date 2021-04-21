package io.zero88.qwe.scheduler.model.job;

import io.zero88.qwe.dto.EnumType;
import io.zero88.qwe.dto.EnumType.AbstractEnumType;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Defines job type
 *
 * @see QWEJobModel
 */
public final class JobType extends AbstractEnumType {

    private JobType(String type) {
        super(type);
    }

    @JsonCreator
    public static JobType factory(String type) {
        return EnumType.factory(type, JobType.class, true);
    }

}
