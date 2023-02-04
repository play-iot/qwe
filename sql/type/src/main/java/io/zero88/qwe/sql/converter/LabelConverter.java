package cloud.playio.qwe.sql.converter;

import java.util.Objects;

import org.jooq.Converter;

import io.github.zero88.utils.Strings;
import cloud.playio.qwe.dto.JsonData;
import cloud.playio.qwe.sql.type.Label;

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
