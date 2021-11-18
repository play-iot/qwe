package io.zero88.qwe.sql.converter;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Objects;

import org.jooq.Converter;

/**
 * Treat {@code LocalDate} from input that is persisted in {@code database} is always {@code java.sql.Date} in {@code
 * UTC} timezone regardless {@code default system timezone}
 */
public final class DateConverter implements Converter<Date, LocalDate> {

    @Override
    public LocalDate from(Date databaseObject) {
        if (Objects.isNull(databaseObject)) {
            return null;
        }
        return databaseObject.toLocalDate()
                             .atStartOfDay(ZoneId.systemDefault())
                             .withZoneSameLocal(ZoneOffset.UTC)
                             .toLocalDate();
    }

    @Override
    public Date to(LocalDate userObject) {
        return Objects.isNull(userObject)
               ? null
               : Date.valueOf(
                   userObject.atStartOfDay(ZoneId.systemDefault()).withZoneSameLocal(ZoneOffset.UTC).toLocalDate());
    }

    @Override
    public Class<Date> fromType() {
        return Date.class;
    }

    @Override
    public Class<LocalDate> toType() {
        return LocalDate.class;
    }

}

