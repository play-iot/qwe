package io.zero88.qwe.sql.converter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.TimeZone;

import org.jooq.Converter;

/**
 * Treat {@code LocalDateTime} from input that is persisted in {@code database} is always {@code timestamp} in {@code
 * UTC} timezone regardless {@code default system timezone}
 */
public final class TimestampLocalConverter implements Converter<Timestamp, LocalDateTime> {

    @Override
    public LocalDateTime from(Timestamp databaseObject) {
        if (Objects.isNull(databaseObject)) {
            return null;
        }
        return databaseObject.toLocalDateTime()
                             .atZone(ZoneId.systemDefault())
                             .toOffsetDateTime()
                             .withOffsetSameLocal(ZoneOffset.UTC)
                             .toLocalDateTime();
    }

    @Override
    public Timestamp to(LocalDateTime userObject) {
        return Objects.isNull(userObject)
               ? null
               : Timestamp.valueOf(userObject.atZone(TimeZone.getDefault().toZoneId())
                                             .withZoneSameLocal(ZoneOffset.UTC)
                                             .toLocalDateTime());
    }

    @Override
    public Class<Timestamp> fromType() {
        return Timestamp.class;
    }

    @Override
    public Class<LocalDateTime> toType() {
        return LocalDateTime.class;
    }

}

