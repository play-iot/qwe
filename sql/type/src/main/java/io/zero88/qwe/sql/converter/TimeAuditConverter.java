package cloud.playio.qwe.sql.converter;

import java.util.Objects;

import org.jooq.Converter;

import io.github.zero88.utils.Strings;
import cloud.playio.qwe.dto.JsonData;
import cloud.playio.qwe.sql.type.TimeAudit;

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
