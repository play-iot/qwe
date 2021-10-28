package io.zero88.qwe.sql.workflow.task;

import io.zero88.jooqx.JsonRecord;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.eventbus.EventAction;
import io.zero88.qwe.sql.EntityMetadata;
import io.zero88.qwe.workflow.TaskExecutionContext;

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
