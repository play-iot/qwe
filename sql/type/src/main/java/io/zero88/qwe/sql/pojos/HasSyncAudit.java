package io.zero88.qwe.sql.pojos;

import io.zero88.qwe.sql.type.SyncAudit;
import io.github.zero88.jooqx.JsonRecord;

public interface HasSyncAudit extends JsonRecord {

    SyncAudit getSyncAudit();

    <T extends HasSyncAudit> T setSyncAudit(SyncAudit value);

}
