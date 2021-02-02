package io.github.zero88.qwe.scheduler.model.trigger;

import io.github.zero88.qwe.dto.JsonData;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
@JsonInclude(Include.NON_EMPTY)
public final class TriggerOption implements JsonData {

    @JsonUnwrapped
    private final TriggerType type;
    /**
     * Cron expression
     *
     * @see TriggerType#CRON
     */
    private final String expression;
    /**
     * Timezone Id
     *
     * @see TriggerType#CRON
     */
    private final String timezone;

    /**
     * Specify a repeat interval in seconds - which will then be multiplied by 1000 to produce milliseconds.
     *
     * @see TriggerType#PERIODIC
     */
    private final Integer intervalInSeconds;
    /**
     * Specify a the number of time the trigger will repeat - total number of firings will be this number + 1.
     *
     * @apiNote repeat forever will be set as -1
     * @see TriggerType#PERIODIC
     */
    @Default
    private final Integer repeat = -1;

}
