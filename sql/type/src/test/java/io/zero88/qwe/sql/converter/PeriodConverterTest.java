package io.zero88.qwe.sql.converter;

import java.time.Period;
import java.util.Objects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.zero88.qwe.sql.exceptions.DBConverterException;

public class PeriodConverterTest {

    private PeriodConverter converter;

    @BeforeEach
    public void before() {
        this.converter = new PeriodConverter();
    }

    @Test
    public void test_from_null() {
        Assertions.assertNull(this.converter.from(null));
    }

    @Test
    public void test_from_invalid_pattern() {
        Assertions.assertThrows(DBConverterException.class, () -> this.converter.from("PT2Y"));
    }

    @Test
    public void test_from_years_months_days() {
        Period period = Objects.requireNonNull(this.converter.from("P2Y3M4W5D"));
        Assertions.assertEquals(period.getYears(), 2);
        Assertions.assertEquals(period.getMonths(), 3);
        Assertions.assertEquals(period.getDays(), 33);
    }

    @Test
    public void test_to_with_null() {
        Assertions.assertNull(this.converter.to(null));
    }

    @Test
    public void test_to_string() {
        Period period = Period.ofYears(2).plusMonths(2).plusDays(30);
        Assertions.assertEquals(this.converter.to(period), "P2Y2M30D");
    }

    @Test
    public void test_from_class() {
        Assertions.assertEquals(this.converter.fromType(), String.class);
    }

    @Test
    public void test_to_class() {
        Assertions.assertEquals(this.converter.toType(), Period.class);
    }

}
