package cloud.playio.qwe.sql.converter;

import java.util.Objects;

import org.jooq.Converter;

import cloud.playio.qwe.dto.EnumType;

public abstract class AbstractEnumConverter<T extends EnumType> implements Converter<String, T> {

    @Override
    public final T from(String databaseObject) {
        return EnumType.factory(databaseObject, toType(), def());
    }

    @Override
    public final String to(T userObject) {
        return Objects.isNull(userObject) ? Objects.isNull(def()) ? null : def().type() : userObject.type();
    }

    @Override
    public final Class<String> fromType() {
        return String.class;
    }

    protected abstract T def();

}
