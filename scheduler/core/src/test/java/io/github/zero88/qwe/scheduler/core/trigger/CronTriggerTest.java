package io.github.zero88.qwe.scheduler.core.trigger;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CronTriggerTest {

    @Test
    void test_invalid_trigger() {
        final CronTrigger trigger = CronTrigger.builder().expression("a 0/2 0 ? * * *").build();
        Assertions.assertThrows(IllegalArgumentException.class, trigger::getCronExpression);
    }

    @Test
    void test_trigger() {
        final CronTrigger trigger = CronTrigger.builder().expression("0 0/2 0 ? * * *").build();
        final CronExpression cronExpression = trigger.getCronExpression();
        final Instant parse = Instant.parse("2021-02-25T00:00:00Z");

        Assertions.assertEquals("GMT", trigger.getTimeZone().getID());
        Assertions.assertEquals(cronExpression.getTimeZone(), trigger.getTimeZone());
        Assertions.assertEquals(2 * 60 * 1000, trigger.nextTriggerAfter(parse));
    }

}
