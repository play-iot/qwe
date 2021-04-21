package io.zero88.qwe.scheduler.model.trigger;

import java.text.ParseException;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.TimeZone;

import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.ScheduleBuilder;
import org.quartz.TriggerKey;

import io.github.zero88.exceptions.ErrorCode;
import io.github.zero88.exceptions.HiddenException;
import io.zero88.qwe.exceptions.CarlException;
import io.zero88.qwe.scheduler.model.trigger.QWETriggerModel.AbstractTriggerModel;
import io.github.zero88.utils.Strings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldNameConstants;
import lombok.extern.jackson.Jacksonized;

@Getter
@Jacksonized
@FieldNameConstants
@Builder(builderClassName = "Builder")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public final class CronTriggerModel extends AbstractTriggerModel {

    @NonNull
    @JsonIgnore
    private final CronExpression expression;
    @NonNull
    @EqualsAndHashCode.Include
    private final TimeZone timezone;

    private CronTriggerModel(TriggerType type, TriggerKey key, CronExpression expression, TimeZone timezone) {
        super(key, type);
        this.expression = expression;
        this.timezone = timezone;
    }

    @Override
    protected @NonNull ScheduleBuilder<CronTrigger> scheduleBuilder() {
        return CronScheduleBuilder.cronSchedule(expression).inTimeZone(timezone);
    }

    @JsonProperty(Fields.expression)
    @EqualsAndHashCode.Include
    private String expr() {
        return expression.getCronExpression();
    }

    @JsonProperty(Fields.timezone)
    private String tz() {
        return timezone.getID();
    }

    @Override
    public String logicalThread() {
        return expr() + "::" + tz();
    }

    public static class Builder extends AbstractTriggerModelBuilder<CronTriggerModel, Builder> {

        @JsonProperty(Fields.expression)
        private String expr;
        @JsonProperty(Fields.timezone)
        private String tz;

        static CronExpression toCronExpr(String expression) {
            try {
                return new CronExpression(expression);
            } catch (IllegalArgumentException | ParseException e) {
                throw new CarlException(ErrorCode.INVALID_ARGUMENT, "Cannot parse cron expression",
                                        new HiddenException(e));
            }
        }

        public Builder tz(String timezone) {
            this.tz = timezone;
            return this;
        }

        public Builder expr(String expression) {
            this.expr = expression;
            return this;
        }

        public CronTriggerModel build() {
            timezone = Objects.nonNull(timezone)
                       ? timezone
                       : TimeZone.getTimeZone(Strings.isBlank(tz) ? ZoneOffset.UTC.getId() : tz);
            expression = Objects.nonNull(expression) ? expression : toCronExpr(expr);
            return new CronTriggerModel(TriggerType.CRON, key(), expression, timezone);
        }

    }

}
