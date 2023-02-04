package cloud.playio.qwe.sql.pojos;

import cloud.playio.qwe.sql.type.TimeAudit;
import io.github.zero88.jooqx.JsonRecord;

public interface HasTimeAudit extends JsonRecord {

    TimeAudit getTimeAudit();

    <T extends HasTimeAudit> T setTimeAudit(TimeAudit value);

}
