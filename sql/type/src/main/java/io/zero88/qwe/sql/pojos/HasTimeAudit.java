package io.zero88.qwe.sql.pojos;

import io.zero88.qwe.sql.type.TimeAudit;
import io.github.zero88.jooqx.JsonRecord;

public interface HasTimeAudit extends JsonRecord {

    TimeAudit getTimeAudit();

    <T extends HasTimeAudit> T setTimeAudit(TimeAudit value);

}
