package cloud.playio.qwe.sql.query;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.jooq.DSLContext;
import org.jooq.ResultQuery;
import org.jooq.UpdatableRecord;

import cloud.playio.qwe.sql.EntityMetadata;
import io.vertx.core.Future;
import io.github.zero88.jooqx.JsonRecord;
import cloud.playio.qwe.dto.jpa.Pagination;
import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.sql.handler.EntityHandler;

import lombok.NonNull;

@SuppressWarnings("unchecked")
class SimpleDaoQueryExecutor<K, P extends JsonRecord, R extends UpdatableRecord<R>, D extends VertxDAO<R, P, K>>
    extends BaseDaoQueryExecutor<P> implements SimpleQueryExecutor<P> {

    SimpleDaoQueryExecutor(EntityHandler handler, EntityMetadata<K, P, R> metadata) {
        super(handler, metadata);
    }

    public EntityMetadata<K, P, R> metadata() {
        return super.metadata();
    }

    @Override
    public @NonNull Future<List<P>> findMany(RequestData reqData) {
        final Pagination paging = Optional.ofNullable(reqData.pagination()).orElse(Pagination.builder().build());
        return dao(metadata()).queryExecutor()
                              .findMany((Function<DSLContext, ResultQuery<R>>) queryBuilder().view(reqData.filter(),
                                                                                                   reqData.sort(),
                                                                                                   paging))
                              .flattenAsObservable(records -> records);
    }

    @Override
    public @NonNull Future<P> findOneByKey(RequestData requestData) {
        final K pk = metadata().parseKey(requestData);
        return dao(metadata()).findOneById(pk)
                              .flatMap(o -> o.map(Single::just).orElse(Single.error(metadata().notFound(pk))));
    }

}
