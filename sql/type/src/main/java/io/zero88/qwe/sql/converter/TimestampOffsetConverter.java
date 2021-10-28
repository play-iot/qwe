package io.zero88.qwe.sql.converter;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Objects;

import org.jooq.Converter;

/**
 * Treat {@code OffsetDataTime} from input that is persisted in {@code database} is always {@code timestamp} with {@code
 * UTC} timezone regardless {@code default system timezone} or given input in another timezone
 */
public final class TimestampOffsetConverter implements Converter<Timestamp, OffsetDateTime> {

    @Override
    public OffsetDateTime from(Timestamp databaseObject) {
        if (Objects.isNull(databaseObject)) {
            return null;
        }
        return databaseObject.toLocalDateTime()
                             .atZone(ZoneId.systemDefault())
                             .toOffsetDateTime()
                             .withOffsetSameLocal(ZoneOffset.UTC);
    }

    @Override
    public Timestamp to(OffsetDateTime userObject) {
        return Objects.isNull(userObject)
               ? null : Timestamp.valueOf(userObject.withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime());
    }

    @Override
    public Class<Timestamp> fromType() {
        return Timestamp.class;
    }

    @Override
    public Class<OffsetDateTime> toType() {
        return OffsetDateTime.class;
    }

}

