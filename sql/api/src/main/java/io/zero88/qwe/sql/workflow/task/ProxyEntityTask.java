package cloud.playio.qwe.sql.workflow.task;

import io.github.zero88.jooqx.JsonRecord;
import cloud.playio.qwe.transport.Transporter;
import cloud.playio.qwe.workflow.ProxyTask;

public interface ProxyEntityTask<DC extends EntityDefinitionContext, P extends JsonRecord, R, T extends Transporter>
    extends EntityTask<DC, P, R>, ProxyTask<DC, EntityRuntimeContext<P>, R, T> {

}
