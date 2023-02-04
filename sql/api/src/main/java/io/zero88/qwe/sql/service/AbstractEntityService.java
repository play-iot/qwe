package cloud.playio.qwe.sql.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.github.zero88.jooqx.JsonRecord;
import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.eventbus.EBContract;
import cloud.playio.qwe.eventbus.EventAction;
import cloud.playio.qwe.sql.EntityMetadata;
import cloud.playio.qwe.sql.handler.EntityHandler;
import cloud.playio.qwe.sql.query.SimpleQueryExecutor;
import cloud.playio.qwe.sql.service.decorator.RequestDecorator;
import cloud.playio.qwe.sql.service.transformer.EntityTransformer;
import cloud.playio.qwe.sql.validation.EntityValidation;
import cloud.playio.qwe.sql.validation.OperationValidator;
import cloud.playio.qwe.sql.workflow.DefaultDMLWorkflow;
import cloud.playio.qwe.sql.workflow.DefaultDQLBatchWorkflow;
import cloud.playio.qwe.sql.workflow.DefaultDQLWorkflow;
import cloud.playio.qwe.sql.workflow.DefaultDeletionWorkflow;
import cloud.playio.qwe.sql.workflow.step.CreationStep;
import cloud.playio.qwe.sql.workflow.step.DeletionStep;
import cloud.playio.qwe.sql.workflow.step.GetManyStep;
import cloud.playio.qwe.sql.workflow.step.GetOneStep;
import cloud.playio.qwe.sql.workflow.step.ModificationStep;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Abstract service to implement {@code CRUD} listeners for {@code database entity}
 *
 * @param <P> Type of {@code JsonRecord}
 * @param <M> Type of {@code EntityMetadata}
 * @see EntityService
 * @see RequestDecorator
 * @see EntityTransformer
 * @since 1.0.0
 */
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public abstract class AbstractEntityService<P extends JsonRecord, M extends EntityMetadata>
    implements SimpleEntityService<P, M>, RequestDecorator, EntityTransformer {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final EntityHandler entityHandler;

    @Override
    public EntityHandler entityHandler() { return entityHandler; }

    @Override
    public @NonNull SimpleQueryExecutor<P> queryExecutor() {
        return SimpleQueryExecutor.create(entityHandler(), context());
    }

    @Override
    public @NonNull RequestDecorator requestDecorator() { return this; }

    @Override
    public @NonNull EntityValidation validation() { return context(); }

    @Override
    public @NonNull EntityTransformer transformer() { return this; }

    @EBContract(action = "GET_LIST")
    public Future<JsonObject> list(RequestData requestData) {
        return DefaultDQLBatchWorkflow.builder()
                                      .action(EventAction.GET_LIST)
                                      .metadata(context())
                                      .normalize(requestDecorator()::onReadingManyResource)
                                      .validator(initGetOneValidator())
                                      .taskManager(taskManager())
                                      .sqlStep(initGetManyStep())
                                      .transformer((req, res) -> transformer().afterList(res))
                                      .build()
                                      .run(requestData);
    }

    @EBContract(action = "GET_ONE")
    public Future<JsonObject> get(RequestData requestData) {
        return DefaultDQLWorkflow.<P>builder()
                                 .action(EventAction.GET_ONE)
                                 .metadata(context())
                                 .normalize(requestDecorator()::onReadingOneResource)
                                 .validator(initGetOneValidator())
                                 .taskManager(taskManager())
                                 .sqlStep(initGetOneStep())
                                 .transformer((req, res) -> transformer().afterGet(res, req))
                                 .build()
                                 .run(requestData);
    }

    @EBContract(action = "CREATE")
    public Future<JsonObject> create(RequestData requestData) {
        return DefaultDMLWorkflow.builder()
                                 .action(EventAction.CREATE)
                                 .metadata(context())
                                 .normalize(requestDecorator()::onCreatingOneResource)
                                 .validator(initCreationValidator())
                                 .taskManager(taskManager())
                                 .sqlStep(initCreationStep())
                                 .transformer((r, p) -> transformer().afterCreate(p.primaryKey(), p.dbEntity(), r))
                                 .build()
                                 .run(requestData);
    }

    @EBContract(action = "UPDATE")
    public Future<JsonObject> update(RequestData requestData) {
        return DefaultDMLWorkflow.builder()
                                 .action(EventAction.UPDATE)
                                 .metadata(context())
                                 .normalize(requestDecorator()::onModifyingOneResource)
                                 .validator(initUpdateValidator())
                                 .taskManager(taskManager())
                                 .sqlStep(initModificationStep(EventAction.UPDATE))
                                 .transformer((r, p) -> transformer().afterUpdate(p.primaryKey(), p.dbEntity(), r))
                                 .build()
                                 .run(requestData);
    }

    @EBContract(action = "PATCH")
    public Future<JsonObject> patch(RequestData requestData) {
        return DefaultDMLWorkflow.builder()
                                 .action(EventAction.PATCH)
                                 .metadata(context())
                                 .normalize(requestDecorator()::onModifyingOneResource)
                                 .validator(initPatchValidator())
                                 .taskManager(taskManager())
                                 .sqlStep(initModificationStep(EventAction.PATCH))
                                 .transformer((r, p) -> transformer().afterPatch(p.primaryKey(), p.dbEntity(), r))
                                 .build()
                                 .run(requestData);
    }

    @EBContract(action = "REMOVE")
    public Future<JsonObject> delete(RequestData requestData) {
        return DefaultDeletionWorkflow.builder()
                                      .action(EventAction.REMOVE)
                                      .metadata(context())
                                      .supportForceDeletion(supportForceDeletion())
                                      .normalize(requestDecorator()::onDeletingOneResource)
                                      .validator(initDeletionValidator())
                                      .taskManager(taskManager())
                                      .sqlStep(initDeletionStep())
                                      .transformer((req, re) -> transformer().afterDelete(re.dbEntity(), req))
                                      .build()
                                      .run(requestData);
    }

    @Override
    public @NonNull EntityMetadata resourceMetadata() {
        return context();
    }

    @NonNull
    protected GetManyStep initGetManyStep() {
        return GetManyStep.builder()
                          .action(EventAction.GET_LIST)
                          .queryExecutor(queryExecutor())
                          .onEach(transformer()::afterEach)
                          .build();
    }

    @NonNull
    protected OperationValidator initGetOneValidator() {
        return OperationValidator.create((req, pojo) -> Future.succeededFuture(pojo));
    }

    @NonNull
    protected <PP extends P> GetOneStep<PP> initGetOneStep() {
        return GetOneStep.<PP>builder().action(EventAction.GET_ONE).queryExecutor(queryExecutor()).build();
    }

    @NonNull
    protected OperationValidator initCreationValidator() {
        return OperationValidator.create((req, dbEntity) -> Future.succeededFuture(validation().onCreating(req)));
    }

    @NonNull
    protected CreationStep initCreationStep() {
        return CreationStep.builder().action(EventAction.CREATE).queryExecutor(queryExecutor()).build();
    }

    @NonNull
    protected OperationValidator initPatchValidator() {
        return OperationValidator.create(
            (req, dbEntity) -> Future.succeededFuture(validation().onPatching(dbEntity, req)));
    }

    @NonNull
    protected OperationValidator initUpdateValidator() {
        return OperationValidator.create(
            (req, dbEntity) -> Future.succeededFuture(validation().onUpdating(dbEntity, req)));
    }

    @NonNull
    protected ModificationStep initModificationStep(@NonNull EventAction action) {
        return ModificationStep.builder().action(action).queryExecutor(queryExecutor()).build();
    }

    @NonNull
    protected OperationValidator initDeletionValidator() {
        return OperationValidator.create(
            (req, dbEntity) -> Future.succeededFuture(validation().onDeleting(dbEntity, req)));
    }

    @NonNull
    protected DeletionStep initDeletionStep() {
        return DeletionStep.builder().action(EventAction.REMOVE).queryExecutor(queryExecutor()).build();
    }

}
