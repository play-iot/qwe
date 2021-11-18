package io.zero88.qwe.sql.query;

import java.util.function.Function;

import org.jooq.DSLContext;
import org.jooq.ResultQuery;
import org.jooq.UpdatableRecord;

import io.zero88.qwe.sql.EntityMetadata;
import io.vertx.core.Future;
import io.zero88.jooqx.JsonRecord;
import io.zero88.qwe.dto.jpa.Sort;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.dto.msg.RequestFilter;
import io.zero88.qwe.sql.handler.EntityHandler;
import io.zero88.qwe.sql.marker.ReferencingEntityMarker;

import lombok.NonNull;

final class ReferencingDaoQueryExecutor<K, P extends JsonRecord, R extends UpdatableRecord<R>, DAO extends VertxDAO<R, P, K>>
    extends SimpleDaoQueryExecutor<K, P, R, DAO> implements ReferencingQueryExecutor<P> {

    private final ReferencingEntityMarker marker;

    ReferencingDaoQueryExecutor(@NonNull EntityHandler handler, @NonNull EntityMetadata<K, P, R> metadata,
                                @NonNull ReferencingEntityMarker marker) {
        super(handler, metadata);
        this.marker = marker;
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NonNull Future<P> findOneByKey(RequestData reqData) {
        final K pk = metadata().parseKey(reqData);
        final RequestFilter filter = reqData.filter();
        final Sort sort = reqData.sort();
        return dao(metadata()).queryExecutor()
                              .findOne((Function<DSLContext, ResultQuery<R>>) queryBuilder().viewOne(filter, sort))
                              .flatMap(o -> o.map(Single::just).orElse(Single.error(metadata().notFound(pk))))
                              .onErrorResumeNext(EntityQueryExecutor::sneakyThrowDBError);
    }

    @Override
    public @NonNull ReferencingEntityMarker marker() {
        return marker;
    }

}
