package io.zero88.qwe.sql.converter;

import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TimeConverterTest {

    private TimeConverter converter;

    @BeforeEach
    public void before() {
        this.converter = new TimeConverter();
    }

    @Test
    public void test_from_null() {
        Assertions.assertNull(this.converter.from(null));
    }

    @Test
    public void test_from_date() {
        LocalTime convertedDate = this.converter.from(Time.valueOf("00:20:20"));
        LocalTime localDate = LocalTime.parse("00:20:20", DateTimeFormatter.ISO_LOCAL_TIME);
        Assertions.assertEquals(localDate, convertedDate);
    }

    @Test
    public void test_from_end_date() {
        LocalTime convertedDate = this.converter.from(Time.valueOf("23:20:20"));
        LocalTime localDate = LocalTime.parse("23:20:20", DateTimeFormatter.ISO_LOCAL_TIME);
        Assertions.assertEquals(localDate, convertedDate);
    }

    @Test
    public void test_to_null() {
        Assertions.assertNull(this.converter.to(null));
    }

    @Test
    public void test_to_local_time() {
        LocalTime localTime = LocalTime.of(8, 18, 26);
        Time convertedTime = this.converter.to(localTime);
        Assertions.assertEquals(Time.valueOf("08:18:26"), convertedTime);
    }

    @Test
    public void test_from_class() {
        Assertions.assertEquals(this.converter.fromType(), Time.class);
    }

    @Test
    public void test_to_class() {
        Assertions.assertEquals(this.converter.toType(), LocalTime.class);
    }

}
