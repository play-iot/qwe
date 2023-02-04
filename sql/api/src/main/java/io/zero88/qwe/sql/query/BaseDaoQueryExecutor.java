package cloud.playio.qwe.sql.query;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.ResultQuery;

import cloud.playio.qwe.sql.EntityMetadata;
import cloud.playio.qwe.sql.pojos.DMLPojo;
import io.vertx.core.Future;
import io.github.zero88.jooqx.JsonRecord;
import cloud.playio.qwe.dto.jpa.Pagination;
import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.sql.handler.AuditDecorator;
import cloud.playio.qwe.sql.handler.EntityHandler;
import cloud.playio.qwe.sql.validation.OperationValidator;
import cloud.playio.qwe.utils.JsonUtils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@SuppressWarnings("unchecked")
abstract class BaseDaoQueryExecutor<P extends JsonRecord> implements InternalQueryExecutor<P> {

    @NonNull
    private final EntityHandler entityHandler;
    @NonNull
    private final EntityMetadata metadata;
    @Setter
    private Configuration runtimeConfiguration;

    public final Configuration runtimeConfiguration() {
        return Optional.ofNullable(runtimeConfiguration).orElseGet(entityHandler.dsl()::configuration);
    }

    @Override
    public QueryBuilder queryBuilder() {
        return new QueryBuilder(metadata);
    }

    @Override
    public @NonNull Future<List<P>> findMany(@NonNull RequestData reqData) {
        final Pagination paging = Optional.ofNullable(reqData.pagination()).orElse(Pagination.builder().build());
        final Function<DSLContext, ? extends ResultQuery<? extends Record>> query = queryBuilder().view(
            reqData.filter(), reqData.sort(), paging);
        return ((Single<List<P>>) dao(metadata).queryExecutor().findMany(query)).flattenAsObservable(rs -> rs);
    }

    @Override
    public @NonNull Future<DMLPojo> insertReturningPrimary(@NonNull RequestData reqData,
                                                           @NonNull OperationValidator validator) {
        final VertxDAO dao = dao(metadata);
        return validator.validate(reqData, null).flatMap(pojo -> {
            final Optional<Object> opt = Optional.ofNullable(pojo.toJson().getValue(metadata.jsonKeyName()))
                                                 .map(k -> metadata.parseKey(k.toString()));
            return opt.map(key -> fetchExists(queryBuilder().exist(metadata, key)).map(b -> key))
                      .orElse(Maybe.empty())
                      .flatMap(k -> Maybe.error(metadata.alreadyExisted(JsonUtils.kvMsg(metadata.requestKeyName(), k))))
                      .switchIfEmpty(Single.just((P) AuditDecorator.addCreationAudit(reqData, metadata, pojo)))
                      .flatMap(entity -> opt.isPresent()
                                         ? ((Single<Integer>) dao.insert(entity)).map(i -> opt.get())
                                         : (Single<?>) dao.insertReturningPrimary(entity))
                      .map(key -> DMLPojo.builder().request(pojo).primaryKey(key).build());
        });
    }

    @Override
    public @NonNull Future<DMLPojo> modifyReturningPrimary(@NonNull RequestData reqData,
                                                           @NonNull OperationValidator validator) {
        final Object pk = metadata.parseKey(reqData);
        final VertxDAO dao = dao(metadata);
        //TODO validate unique keys
        return findOneByKey(reqData).flatMap(db -> validator.validate(reqData, db).map(p -> new SimpleEntry<>(db, p)))
                                    .map(entry -> AuditDecorator.addModifiedAudit(reqData, metadata, entry.getKey(),
                                                                                  entry.getValue()))
                                    .flatMapMaybe(p -> ((Single<Integer>) dao.update(p)).filter(i -> i > 0).map(r -> p))
                                    .switchIfEmpty(Single.error(metadata.notFound(pk)))
                                    .map(p -> DMLPojo.builder().request(p).primaryKey(pk).build());
    }

    @Override
    public @NonNull Future<P> deleteOneByKey(@NonNull RequestData reqData, @NonNull OperationValidator validator) {
        return findOneByKey(reqData).flatMap(db -> validator.validate(reqData, db)).flatMap(db -> doDelete((P) db));
    }

    @Override
    public final @NonNull <X> Future<X> executeAny(@NonNull Function<DSLContext, X> function) {
        //TODO
        return Single.just(null);
//        return (Single<X>) entityHandler().genericQuery(runtimeConfiguration()).execute(function);
    }

    private Single<P> doDelete(@NonNull P dbEntity) {
        final Object pk = metadata.parseKey(dbEntity);
        final Single<Integer> delete = (Single<Integer>) dao(metadata).deleteById(pk);
        return delete.filter(r -> r > 0)
                     .map(r -> dbEntity)
                     .switchIfEmpty(EntityQueryExecutor.unableDelete(JsonUtils.kvMsg(metadata.requestKeyName(), pk)));
    }

}
