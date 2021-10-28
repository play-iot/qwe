package io.zero88.qwe.sql.converter;

import java.util.Objects;

import org.jooq.Converter;

import io.github.zero88.utils.Strings;
import io.zero88.qwe.dto.JsonData;
import io.zero88.qwe.sql.type.Label;

public final class LabelConverter implements Converter<String, Label> {

    @Override
    public Label from(String databaseObject) {
        return Strings.isBlank(databaseObject) ? null : JsonData.from(databaseObject, Label.class);
    }

    @Override
    public String to(Label userObject) {
        return Objects.isNull(userObject) ? null : userObject.toJson().encode();
    }

    @Override
    public Class<String> fromType() {
        return String.class;
    }

    @Override
    public Class<Label> toType() {
        return Label.class;
    }

}
