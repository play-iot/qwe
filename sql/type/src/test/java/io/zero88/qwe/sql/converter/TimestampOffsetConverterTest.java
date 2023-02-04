package cloud.playio.qwe.sql.converter;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TimestampOffsetConverterTest {

    private TimestampOffsetConverter converter;

    @BeforeEach
    public void before() {
        this.converter = new TimestampOffsetConverter();
    }

    @Test
    public void test_from_null() {
        Assertions.assertNull(this.converter.from(null));
    }

    @Test
    public void test_from_timezone_utc() {
        Timestamp timestamp = Timestamp.valueOf("2018-12-03 05:15:30");
        OffsetDateTime fromValue = this.converter.from(timestamp);
        Assertions.assertEquals(ZoneOffset.UTC, fromValue.getOffset());
        OffsetDateTime expected = OffsetDateTime.parse("2018-12-03T05:15:30+00:00",
                                                       DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        Assertions.assertEquals(expected, fromValue);
    }

    @Test
    public void test_to_null() {
        Assertions.assertNull(this.converter.to(null));
    }

    @Test
    public void test_to_timezone() {
        OffsetDateTime dateTime = OffsetDateTime.parse("2018-12-03T05:15:30+00:00",
                                                       DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        Timestamp toValue = this.converter.to(dateTime);
        Timestamp expected = Timestamp.valueOf("2018-12-03 05:15:30");
        Assertions.assertEquals(expected, toValue);
    }

    @Test
    public void test_to_timezone_not_utc() {
        OffsetDateTime dateTime = OffsetDateTime.parse("2018-12-03T05:15:30+07:00",
                                                       DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        Timestamp toValue = this.converter.to(dateTime);
        Timestamp expected = Timestamp.valueOf(dateTime.withOffsetSameInstant(ZoneOffset.UTC)
                                                       .atZoneSimilarLocal(ZoneId.systemDefault())
                                                       .toLocalDateTime());
        Assertions.assertEquals(expected, toValue);
    }

    @Test
    public void test_from_class() {
        Assertions.assertEquals(this.converter.fromType(), Timestamp.class);
    }

    @Test
    public void test_to_class() {
        Assertions.assertEquals(this.converter.toType(), OffsetDateTime.class);
    }

}
