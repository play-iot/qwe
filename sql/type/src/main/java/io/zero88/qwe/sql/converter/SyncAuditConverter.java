package io.zero88.qwe.sql.converter;

import java.util.Objects;

import org.jooq.Converter;

import io.github.zero88.utils.Strings;
import io.zero88.qwe.dto.JsonData;
import io.zero88.qwe.sql.type.SyncAudit;

public final class SyncAuditConverter implements Converter<String, SyncAudit> {

    @Override
    public SyncAudit from(String databaseObject) {
        return Strings.isBlank(databaseObject) ? null : JsonData.from(databaseObject, SyncAudit.class);
    }

    @Override
    public String to(SyncAudit userObject) {
        return Objects.isNull(userObject) ? null : userObject.toJson().encode();
    }

    @Override
    public Class<String> fromType() {
        return String.class;
    }

    @Override
    public Class<SyncAudit> toType() {
        return SyncAudit.class;
    }

}
