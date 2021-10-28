package io.zero88.qwe.sql.workflow.task;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.jooq.Condition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.zero88.utils.Functions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zero88.jooqx.JsonRecord;
import io.zero88.qwe.dto.JsonData;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.eventbus.EventAction;
import io.zero88.qwe.eventbus.EventMessage;
import io.zero88.qwe.sql.EntityMetadata;
import io.zero88.qwe.sql.ReferenceEntityMetadata;
import io.zero88.qwe.sql.pojos.DMLPojo;
import io.zero88.qwe.sql.service.cache.EntityServiceIndex;
import io.zero88.qwe.sql.workflow.task.EntityTask.EntityPurgeTask;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@SuppressWarnings("unchecked")
final class DefaultEntityPurgeTask<P extends JsonRecord>
    implements EntityPurgeTask<PurgeDefinitionContext, P, DMLPojo> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityPurgeTask.class.getName());
    @NonNull
    private final PurgeDefinitionContext definitionContext;
    private final boolean supportForceDeletion;

    @Override
    public @NonNull Future<Boolean> isExecutable(@NonNull EntityRuntimeContext<P> runtimeContext) {
        return Future.succeededFuture(runtimeContext.getOriginReqAction() == EventAction.REMOVE);
    }

    @Override
    public @NonNull Future<@Nullable DMLPojo> execute(@NonNull EntityRuntimeContext<P> runtimeContext) {
        final @NonNull EntityMetadata metadata = runtimeContext.getMetadata();
        final @NonNull RequestData reqData = runtimeContext.getOriginReqData();
        final P pojo = runtimeContext.getData();
        final Object pk = metadata.parseKey(pojo);
        final DMLPojo dmlPojo = DMLPojo.builder().request(pojo).primaryKey(pk).build();
        if (!supportForceDeletion || !reqData.filter().hasForce()) {
            return definitionContext.queryExecutor().isAbleToDelete(pojo, metadata).map(b -> dmlPojo);
        }
        final @NonNull EntityServiceIndex index = definitionContext.entityServiceIndex();
        return definitionContext.entityHandler()
                                .holder()
                                .referenceTo(metadata)
                                .stream()
                                .map(ref -> invokeReferenceService(index, reqData, ref, pk))
                                .reduce(Long::sum)
                                .map(t -> new JsonObject().put("total", t))
                                .map(r -> dmlPojo)
                                .defaultIfEmpty(dmlPojo);
    }

    private Future<Long> invokeReferenceService(@NonNull EntityServiceIndex index, @NonNull RequestData reqData,
                                                @NonNull ReferenceEntityMetadata ref, @NonNull Object pk) {
        final EntityMetadata refMetadata = ref.findByTable(definitionContext().entityHandler().metadataIndex());
        final Condition eq = ref.getField().eq(pk);
        return Functions.getIfThrow(() -> index.lookupApiAddress(refMetadata))
                        .map(address -> invokeRemoteDeletion(reqData, refMetadata, eq, address))
                        .orElseGet(() -> deleteDirectly(refMetadata, eq));
    }

    //TODO temporary way to batch delete. Not safe. Must implement ASAP https://github.com/NubeIO/iot-engine/issues/294
    private Future<Long> deleteDirectly(@NonNull EntityMetadata refMetadata, @NonNull Condition eq) {
        LOGGER.debug("Not safe function when purging resources directly");
        return ((Future<Integer>) definitionContext().queryExecutor().dao(refMetadata).deleteByCondition(eq)).map(
            Long::valueOf);
    }

    private Future<Long> invokeRemoteDeletion(@NonNull RequestData reqData, @NonNull EntityMetadata refMetadata,
                                              @NonNull Condition eq, @NonNull String address) {
        final Future<List<JsonRecord>> result = (Future<List<JsonRecord>>) definitionContext().queryExecutor()
                                                                                              .dao(refMetadata)
                                                                                              .findManyByCondition(eq);
        return result.flattenAsObservable(rs -> rs)
                     .map(refMetadata::parseKey)
                     .map(rpk -> RequestData.builder()
                                            .filter(reqData.filter())
                                            .body(new JsonObject().put(refMetadata.requestKeyName(),
                                                                       JsonData.checkAndConvert(rpk)))
                                            .build())
                     .map(req -> EventMessage.initial(EventAction.REMOVE, req))
                     .flatMapSingle(msg -> transporter().request(address, msg))
                     .map(this::handleResult)
                     .count();
    }

    private JsonObject handleResult(@NonNull EventMessage message) {
        LOGGER.debug(message.toJson().encode());
        if (message.isError()) {
            return message.getError().toJson();
        }
        return message.getData();
    }

}
