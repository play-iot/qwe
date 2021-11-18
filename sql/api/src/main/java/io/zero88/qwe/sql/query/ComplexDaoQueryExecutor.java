package io.zero88.qwe.sql.query;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.ResultQuery;

import io.github.jklingsporn.vertx.jooq.rx.jdbc.JDBCRXGenericQueryExecutor;
import io.zero88.jooqx.JsonRecord;
import io.zero88.qwe.sql.CompositeMetadata;
import io.zero88.qwe.sql.EntityMetadata;
import io.zero88.qwe.sql.pojos.CompositePojo;
import io.zero88.qwe.sql.pojos.DMLPojo;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zero88.qwe.dto.JsonData;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.dto.msg.RequestFilter;
import io.zero88.qwe.sql.handler.AuditDecorator;
import io.zero88.qwe.sql.handler.EntityHandler;
import io.zero88.qwe.sql.marker.EntityReferences;
import io.zero88.qwe.sql.marker.ReferencingEntityMarker;
import io.zero88.qwe.sql.validation.OperationValidator;
import io.zero88.qwe.utils.JsonUtils;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

@SuppressWarnings("unchecked")
@Accessors(fluent = true)
final class ComplexDaoQueryExecutor<CP extends CompositePojo> extends JDBCRXGenericQueryExecutor
    implements ComplexQueryExecutor<CP>, InternalQueryExecutor<CP> {

    @NonNull
    @Getter
    private final EntityHandler entityHandler;
    @Setter
    private Configuration runtimeConfiguration;
    private CompositeMetadata base;
    private EntityMetadata context;
    private final Predicate<EntityMetadata> existPredicate = m -> Objects.nonNull(context) && !m.singularKeyName()
                                                                                                .equals(
                                                                                                    context.singularKeyName());
    private EntityMetadata resource;
    private Predicate<EntityMetadata> viewPredicate = existPredicate;
    private EntityReferences references;

    ComplexDaoQueryExecutor(@NonNull EntityHandler entityHandler) {
        super(entityHandler.dsl().configuration(), entityHandler.vertx());
        this.entityHandler = entityHandler;
    }

    @Override
    public ComplexQueryExecutor from(@NonNull CompositeMetadata metadata) {
        this.base = metadata;
        this.context = Optional.ofNullable(context).orElse(metadata);
        return this;
    }

    @Override
    public ComplexQueryExecutor with(@NonNull EntityMetadata resourceMetadata) {
        this.resource = Optional.ofNullable(resource).orElse(resourceMetadata);
        return this;
    }

    @Override
    public ComplexQueryExecutor context(@NonNull EntityMetadata contextMetadata) {
        this.context = contextMetadata;
        return this;
    }

    @Override
    public ComplexQueryExecutor references(@NonNull EntityReferences references) {
        this.references = references;
        return this;
    }

    @Override
    public ComplexQueryExecutor viewPredicate(@NonNull Predicate<EntityMetadata> predicate) {
        this.viewPredicate = predicate;
        return this;
    }

    @Override
    public Configuration configuration() {
        return runtimeConfiguration();
    }

    public Configuration runtimeConfiguration() {
        return Optional.ofNullable(runtimeConfiguration).orElseGet(entityHandler.dsl()::configuration);
    }

    @Override
    public QueryBuilder queryBuilder() {
        return new QueryBuilder(base).references(Arrays.asList(resource, context)).predicate(viewPredicate);
    }

    @Override
    public @NonNull Future<List<CP>> findMany(@NonNull RequestData requestData) {
        return executeAny(queryBuilder().view(requestData.filter(), requestData.sort(), requestData.pagination())).map(
            r -> r.fetch(toMapper())).flattenAsObservable(s -> s);
    }

    @Override
    public @NonNull Future<CP> findOneByKey(@NonNull RequestData requestData) {
        final RequestFilter filter = requestData.filter();
        final Single<? extends ResultQuery<? extends Record>> result = executeAny(
            queryBuilder().viewOne(filter, requestData.sort()));
        return result.map(r -> Optional.ofNullable(r.fetchOne(toMapper())))
                     .filter(Optional::isPresent)
                     .switchIfEmpty(Single.error(base.notFound(base.msg(filter, references.keys()))))
                     .map(Optional::get)
                     .onErrorResumeNext(EntityQueryExecutor::sneakyThrowDBError);
    }

    @Override
    public @NonNull Future<CP> lookupByPrimaryKey(@NonNull Object primaryKey) {
        final RequestFilter filter = new RequestFilter().put(base.jsonKeyName(), JsonData.checkAndConvert(primaryKey));
        return executeAny(queryBuilder().viewOne(filter, null)).map(r -> Optional.ofNullable(r.fetchOne(toMapper())))
                                                               .filter(Optional::isPresent)
                                                               .switchIfEmpty(Single.error(base.notFound(primaryKey)))
                                                               .map(Optional::get)
                                                               .flatMap(Single::just);
    }

    @Override
    public @NonNull Future<DMLPojo> insertReturningPrimary(@NonNull RequestData reqData,
                                                           @NonNull OperationValidator validator) {
        return validator.validate(reqData, null).map(pojo -> (CP) pojo).flatMap(pojo -> {
            final JsonRecord src = pojo.safeGetOther(resource.singularKeyName(), resource.modelClass());
            final Object sKey = Optional.ofNullable(src)
                                        .map(r -> getKey(r.toJson(), resource))
                                        .orElse(resource.getKey(reqData)
                                                        .orElse(getKey(
                                                            JsonData.safeGet(reqData.body(), resource.singularKeyName(),
                                                                             JsonObject.class), resource)));
            final Object cKey = context.parseKey(reqData);
            if (Objects.isNull(src)) {
                if (Objects.isNull(sKey)) {
                    return Single.error(
                        new IllegalArgumentException("Missing " + resource.singularKeyName() + " data"));
                }
                final RequestFilter filter = reqData.filter();
                return isAbleToInsert(cKey, sKey, filter).map(k -> AuditDecorator.addCreationAudit(reqData, base, pojo))
                                                         .flatMap(p -> doInsertReturnKey(base, p,
                                                                                         getKey(p.toJson(), base)).map(
                                                             k -> DMLPojo.builder().request(p).primaryKey(k).build()));
            }
            final Maybe<Boolean> isExist = fetchExists(queryBuilder().exist(context, cKey));
            final String sKeyN = resource.requestKeyName();
            return isExist.flatMapSingle(b -> lookupByPrimaryKey(resource, sKey))
                          .filter(Optional::isPresent)
                          .flatMap(o -> Maybe.error(resource.alreadyExisted(JsonUtils.kvMsg(sKeyN, sKey))))
                          .switchIfEmpty(Single.just(AuditDecorator.addCreationAudit(reqData, resource, src))
                                               .flatMap(p -> doInsertReturnKey(resource, p, sKey)))
                          .map(k -> AuditDecorator.addCreationAudit(reqData, base,
                                                                    pojo.with(references.get(resource), k)))
                          .flatMap(p -> doInsertReturnKey(base, p, getKey(p.toJson(), base)).map(
                              k -> DMLPojo.builder().request(p).primaryKey(k).build()));
        });
    }

    @Override
    public @NonNull Future<DMLPojo> modifyReturningPrimary(@NonNull RequestData req,
                                                           @NonNull OperationValidator validator) {
        return findOneByKey(req).flatMap(db -> validator.validate(req, db))
                                .map(p -> (CP) p)
                                .flatMap(p -> Optional.ofNullable(p.getOther(resource.singularKeyName()))
                                                      .map(JsonRecord.class::cast)
                                                      .map(r -> (Single) dao(resource).update(
                                                          AuditDecorator.addModifiedAudit(req, resource, r)))
                                                      .orElse(Single.just(p))
                                                      .map(r -> p))
                                .flatMap(p -> {
                                    final CP cp = AuditDecorator.addModifiedAudit(req, base, (CP) p);
                                    final Object pk = base.parseKey(cp);
                                    return ((Single<Integer>) dao(base).update(cp)).map(
                                        i -> DMLPojo.builder().request(cp).primaryKey(pk).build());
                                });
    }

    @Override
    public @NonNull Future<CP> deleteOneByKey(@NonNull RequestData reqData, @NonNull OperationValidator validator) {
        return findOneByKey(reqData).flatMap(dbEntity -> validator.validate(reqData, dbEntity))
                                    .map(dbEntity -> (CP) dbEntity)
                                    .flatMap(dbEntity -> doDelete(reqData, dbEntity));
    }

    @Override
    @NonNull
    public String requestKeyAsMessage(@NonNull EntityMetadata metadata, @NonNull JsonRecord pojo,
                                      @NonNull Object primaryKey) {
        return base.msg(pojo.toJson(), references.keys());
    }

    @Override
    public @NonNull ReferencingEntityMarker marker() {
        throw new UnsupportedOperationException("Not using it in case of many-to-many");
    }

    @Override
    public EntityMetadata metadata() {
        return base;
    }

    private Object getKey(JsonObject data, @NonNull EntityMetadata metadata) {
        return Optional.ofNullable(data).map(d -> d.getValue(metadata.jsonKeyName())).orElse(null);
    }

    private Single<Boolean> isAbleToInsert(@NonNull Object ctxKey, @NonNull Object resourceKey,
                                           @NonNull RequestFilter filter) {
        final QueryBuilder qb = queryBuilder().predicate(existPredicate);
        return fetchExists(qb.exist(context, ctxKey)).switchIfEmpty(Single.error(context.notFound(ctxKey)))
                                                     .flatMapMaybe(b -> fetchExists(qb.exist(resource, resourceKey)))
                                                     .switchIfEmpty(Single.error(resource.notFound(resourceKey)))
                                                     .flatMap(e -> executeAny(qb.existQueryByJoin(filter)))
                                                     .map(r -> Optional.ofNullable(r.fetchOne(toMapper()))
                                                                       .map(cp -> cp.prop(base.jsonKeyName())))
                                                     .filter(op -> !op.isPresent())
                                                     .switchIfEmpty(Single.error(
                                                         base.alreadyExisted(base.msg(filter, references.keys()))))
                                                     .onErrorResumeNext(EntityQueryExecutor::sneakyThrowDBError)
                                                     .map(cp -> true);
    }

    private RecordMapper<? super Record, CP> toMapper() {
        return Objects.requireNonNull(base).mapper(resource, context);
    }

    private Single<? extends CP> doDelete(@NonNull RequestData reqData, @NonNull CP pojo) {
        final Object key = base.parseKey(pojo);
        final Condition c = queryBuilder().conditionByPrimary(base, key);
        final Single<Integer> result = (Single<Integer>) dao(base).deleteByCondition(c);
        return result.filter(r -> r > 0)
                     .map(r -> pojo)
                     .switchIfEmpty(EntityQueryExecutor.unableDelete(base.msg(reqData.filter(), references.keys())));
    }

    private Single<?> doInsertReturnKey(@NonNull EntityMetadata metadata, @NonNull JsonRecord pojo, Object sKey) {
        return Objects.isNull(sKey)
               ? (Single<?>) dao(metadata).insertReturningPrimary(pojo)
               : ((Single<Integer>) dao(metadata).insert(pojo)).map(r -> sKey);
    }

}
