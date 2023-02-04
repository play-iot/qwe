package cloud.playio.qwe.sql.pojos;

import cloud.playio.qwe.sql.type.SyncAudit;
import io.github.zero88.jooqx.JsonRecord;

public interface HasSyncAudit extends JsonRecord {

    SyncAudit getSyncAudit();

    <T extends HasSyncAudit> T setSyncAudit(SyncAudit value);

}
