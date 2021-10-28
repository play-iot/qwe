package io.zero88.qwe.sql.workflow.task;

import io.zero88.jooqx.JsonRecord;
import io.zero88.qwe.transport.Transporter;
import io.zero88.qwe.workflow.ProxyTask;

public interface ProxyEntityTask<DC extends EntityDefinitionContext, P extends JsonRecord, R, T extends Transporter>
    extends EntityTask<DC, P, R>, ProxyTask<DC, EntityRuntimeContext<P>, R, T> {

}
