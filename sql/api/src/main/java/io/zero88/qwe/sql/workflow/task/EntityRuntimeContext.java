package cloud.playio.qwe.sql.workflow.task;

import io.github.zero88.jooqx.JsonRecord;
import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.eventbus.EventAction;
import cloud.playio.qwe.sql.EntityMetadata;
import cloud.playio.qwe.workflow.TaskExecutionContext;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder(builderClassName = "Builder")
public final class EntityRuntimeContext<P extends JsonRecord> implements TaskExecutionContext<P> {

    @NonNull
    private final RequestData originReqData;
    @NonNull
    private final EventAction originReqAction;
    @NonNull
    private final EntityMetadata metadata;
    private final P data;
    private final Throwable throwable;

}
