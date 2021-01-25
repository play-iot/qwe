package io.github.zero88.qwe.scheduler.trigger;

import io.github.zero88.qwe.dto.EnumType;
import io.github.zero88.qwe.dto.EnumType.AbstractEnumType;

import com.fasterxml.jackson.annotation.JsonCreator;

public final class TriggerType extends AbstractEnumType {

    public static final TriggerType CRON = new TriggerType("CRON");
    public static final TriggerType PERIODIC = new TriggerType("PERIODIC");

    private TriggerType(String type) {
        super(type);
    }

    @JsonCreator
    public static TriggerType factory(String type) {
        return EnumType.factory(type, TriggerType.class);
    }

}
