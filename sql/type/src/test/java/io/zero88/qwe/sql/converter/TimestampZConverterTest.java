package cloud.playio.qwe.sql.converter;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TimestampZConverterTest {

    private TimestampZConverter converter;

    @BeforeEach
    public void before() {
        this.converter = new TimestampZConverter();
    }

    @Test
    public void test_from_null() {
        Assertions.assertNull(this.converter.from(null));
    }

    @Test
    public void test_from_timezone() {
        //Timestamp always has no timezone
        Timestamp timestamp = Timestamp.valueOf("2018-12-03 05:15:30");
        Instant instant = this.converter.from(timestamp);

        Instant expected = Instant.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse("2018-12-03T05:15:30+00:00"));

        Assertions.assertEquals(expected, instant);
    }

    @Test
    public void test_to_null() {
        Assertions.assertNull(this.converter.to(null));
    }

    @Test
    public void test_to_timezone() {
        Instant instant = Instant.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse("2018-12-03T05:15:30+00:00"));
        Timestamp localTimestamp = this.converter.to(instant);
        Timestamp timestamp = Timestamp.valueOf("2018-12-03 05:15:30");
        Assertions.assertEquals(localTimestamp, timestamp);
    }

    @Test
    public void test_from_class() {
        Assertions.assertEquals(this.converter.fromType(), Timestamp.class);
    }

    @Test
    public void test_to_class() {
        Assertions.assertEquals(this.converter.toType(), Instant.class);
    }

}
