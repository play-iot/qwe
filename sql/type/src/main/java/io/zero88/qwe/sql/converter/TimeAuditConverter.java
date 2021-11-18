package io.zero88.qwe.sql.converter;

import java.util.Objects;

import org.jooq.Converter;

import io.github.zero88.utils.Strings;
import io.zero88.qwe.dto.JsonData;
import io.zero88.qwe.sql.type.TimeAudit;

public final class TimeAuditConverter implements Converter<String, TimeAudit> {

    @Override
    public TimeAudit from(String databaseObject) {
        return Strings.isBlank(databaseObject) ? null : JsonData.from(databaseObject, TimeAudit.class);
    }

    @Override
    public String to(TimeAudit userObject) {
        return Objects.isNull(userObject) ? null : userObject.toJson().encode();
    }

    @Override
    public Class<String> fromType() {
        return String.class;
    }

    @Override
    public Class<TimeAudit> toType() {
        return TimeAudit.class;
    }

}
