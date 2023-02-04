package cloud.playio.qwe.sql.query;

import java.util.function.Function;

import org.jooq.DSLContext;
import org.jooq.ResultQuery;
import org.jooq.UpdatableRecord;

import cloud.playio.qwe.sql.EntityMetadata;
import io.vertx.core.Future;
import io.github.zero88.jooqx.JsonRecord;
import cloud.playio.qwe.dto.jpa.Sort;
import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.dto.msg.RequestFilter;
import cloud.playio.qwe.sql.handler.EntityHandler;
import cloud.playio.qwe.sql.marker.ReferencingEntityMarker;

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
