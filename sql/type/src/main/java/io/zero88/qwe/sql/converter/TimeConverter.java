package cloud.playio.qwe.sql.converter;

import java.sql.Time;
import java.time.LocalTime;
import java.util.Objects;

import org.jooq.Converter;

public final class TimeConverter implements Converter<Time, LocalTime> {

    @Override
    public LocalTime from(Time databaseObject) {
        if (Objects.isNull(databaseObject)) {
            return null;
        }
        return databaseObject.toLocalTime();
    }

    @Override
    public Time to(LocalTime userObject) {
        return Objects.isNull(userObject) ? null : Time.valueOf(userObject);
    }

    @Override
    public Class<Time> fromType() {
        return Time.class;
    }

    @Override
    public Class<LocalTime> toType() {
        return LocalTime.class;
    }

}

