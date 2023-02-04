package cloud.playio.qwe.sql.query;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.ResultQuery;
import org.jooq.UpdatableRecord;

import cloud.playio.qwe.sql.CompositeMetadata;
import cloud.playio.qwe.sql.EntityMetadata;
import cloud.playio.qwe.sql.pojos.CompositePojo;
import io.vertx.core.Future;
import io.github.zero88.jooqx.JsonRecord;
import cloud.playio.qwe.dto.jpa.Pagination;
import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.sql.handler.EntityHandler;
import cloud.playio.qwe.sql.marker.GroupReferencingEntityMarker;
import io.vertx.core.json.JsonObject;

import lombok.NonNull;

final class GroupDaoQueryExecutor<K, P extends JsonRecord, R extends UpdatableRecord<R>, D extends VertxDAO<R, P, K>,
                                     CP extends CompositePojo<P, CP>>
    extends BaseDaoQueryExecutor<CP> implements GroupQueryExecutor<CP> {

    private final CompositeMetadata<K, P, R, CP> groupMetadata;
    private final GroupReferencingEntityMarker marker;

    GroupDaoQueryExecutor(@NonNull EntityHandler handler, @NonNull EntityMetadata<K, P, R> metadata,
                          @NonNull CompositeMetadata<K, P, R, CP> groupMetadata,
                          @NonNull GroupReferencingEntityMarker marker) {
        super(handler, metadata);
        this.groupMetadata = groupMetadata;
        this.marker = marker;
    }

    @Override
    public QueryBuilder queryBuilder() {
        return new QueryBuilder(groupMetadata).references(groupMetadata.subItems());
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NonNull Future<List<CP>> findMany(RequestData reqData) {
        final Pagination paging = Optional.ofNullable(reqData.pagination()).orElse(Pagination.builder().build());
        final Single<List> many = (Single<List>) dao(metadata()).queryExecutor()
                                                                .findMany(queryBuilder().view(reqData.filter(),
                                                                                              reqData.sort(), paging));
        return many.flattenAsObservable(rs -> rs)
                   .map(pojo -> CompositePojo.create(pojo, groupMetadata.rawClass(), groupMetadata.modelClass()));
    }

    @Override
    public @NonNull GroupReferencingEntityMarker marker() {
        return marker;
    }

    @Override
    public @NonNull Future<CP> findOneByKey(RequestData reqData) {
        final Function<DSLContext, ? extends ResultQuery<? extends Record>> f = queryBuilder().viewOne(reqData.filter(),
                                                                                                       reqData.sort());
        return executeAny(f).map(r -> Optional.ofNullable(r.fetchOne(groupMetadata.mapper())))
                            .filter(Optional::isPresent)
                            .switchIfEmpty(Single.error(metadata().notFound(metadata().parseKey(reqData))))
                            .map(Optional::get)
                            .onErrorResumeNext(EntityQueryExecutor::sneakyThrowDBError);
    }

    @Override
    public @NonNull Future<CP> lookupByPrimaryKey(@NonNull Object primaryKey) {
        final JsonObject filter = new JsonObject().put(metadata().requestKeyName(), primaryKey.toString())
                                                  .put(metadata().jsonKeyName(), primaryKey.toString());
        return findOneByKey(RequestData.builder().body(filter).filter(filter).build());
    }

}
