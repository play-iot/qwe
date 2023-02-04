package cloud.playio.qwe.sql.converter;

import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.Objects;

import org.jooq.Converter;

import cloud.playio.qwe.sql.exceptions.DBConverterException;

public final class PeriodConverter implements Converter<String, Period> {

    @Override
    public Period from(String databaseObject) {
        if (Objects.isNull(databaseObject)) {
            return null;
        }
        try {
            return Period.parse(databaseObject);
        } catch (DateTimeParseException e) {
            throw new DBConverterException("Wrong Period data format: " + databaseObject, e);
        }
    }

    @Override
    public String to(Period userObject) {
        if (Objects.isNull(userObject)) {
            return null;
        }
        return userObject.toString();
    }

    @Override
    public Class<String> fromType() { return String.class; }

    @Override
    public Class<Period> toType() { return Period.class; }

}
