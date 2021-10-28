package io.zero88.qwe.sql.converter;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DateConverterTest {

    private DateConverter converter;

    @BeforeEach
    public void before() {
        this.converter = new DateConverter();
    }

    @Test
    public void test_from_null() {
        Assertions.assertNull(this.converter.from(null));
    }

    @Test
    public void test_from_date() {
        LocalDate expected = LocalDate.parse("2019-02-01", DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate convertedDate = this.converter.from(Date.valueOf("2019-02-01"));
        Assertions.assertEquals(expected, convertedDate);
    }

    @Test
    public void test_to_null() {
        Assertions.assertNull(this.converter.to(null));
    }

    @Test
    public void test_to_date() {
        final Date expected = Date.valueOf("2019-02-01");

        LocalDate localDate = LocalDate.parse("2019-02-01", DateTimeFormatter.ISO_LOCAL_DATE);
        Date convertedDate = this.converter.to(localDate);

        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
        Assertions.assertEquals(df.format(expected), df.format(convertedDate));

        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
        Assertions.assertEquals(df2.format(expected), df2.format(convertedDate));
    }

    @Test
    public void test_from_class() {
        Assertions.assertEquals(this.converter.fromType(), Date.class);
    }

    @Test
    public void test_to_class() {
        Assertions.assertEquals(this.converter.toType(), LocalDate.class);
    }

}
