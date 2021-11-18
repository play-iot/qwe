package io.zero88.qwe.sql.converter;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Objects;

import org.jooq.Converter;

/**
 * Treat {@code Instant} from input that is persisted in {@code database} is always {@code timestamp} with {@code UTC}
 * timezone regardless {@code default system timezone}
 */
public final class TimestampZConverter implements Converter<Timestamp, Instant> {

    @Override
    public Instant from(Timestamp databaseObject) {
        if (Objects.isNull(databaseObject)) {
            return null;
        }
        return databaseObject.toLocalDateTime()
                             .atZone(ZoneId.systemDefault())
                             .toOffsetDateTime()
                             .withOffsetSameLocal(ZoneOffset.UTC)
                             .toInstant();
    }

    @Override
    public Timestamp to(Instant userObject) {
        return Objects.isNull(userObject)
               ? null
               : Timestamp.valueOf(userObject.atOffset(ZoneOffset.UTC).toLocalDateTime());
    }

    @Override
    public Class<Timestamp> fromType() {
        return Timestamp.class;
    }

    @Override
    public Class<Instant> toType() {
        return Instant.class;
    }

}

