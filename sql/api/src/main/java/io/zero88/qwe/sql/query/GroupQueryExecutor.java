package io.zero88.qwe.sql.query;

import java.util.Optional;

import org.jooq.UpdatableRecord;

import io.zero88.qwe.sql.CompositeMetadata;
import io.zero88.qwe.sql.EntityMetadata;
import io.zero88.qwe.sql.pojos.CompositePojo;
import io.vertx.core.Future;
import io.zero88.jooqx.JsonRecord;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.sql.handler.EntityHandler;
import io.zero88.qwe.sql.marker.EntityReferences;
import io.zero88.qwe.sql.marker.GroupReferencingEntityMarker;
import io.vertx.core.json.JsonObject;

import lombok.NonNull;

/**
 * Represents for a {@code sql executor} do {@code DML} or {@code DQL} on {@code group entity}.
 *
 * @param <CP> Type of {@code CompositePojo}
 * @see GroupReferencingEntityMarker
 * @since 1.0.0
 */
public interface GroupQueryExecutor<CP extends CompositePojo> extends ReferencingQueryExecutor<CP> {

    /**
     * Create group query executor.
     *
     * @param <K>               Type of {@code primary key}
     * @param <P>               Type of {@code JsonRecord}
     * @param <R>               Type of {@code UpdatableRecord}
     * @param <D>               Type of {@code VertxDAO}
     * @param <CP>              Type of {@code CompositePojo}
     * @param handler           the entity handler
     * @param metadata          the metadata
     * @param compositeMetadata the composite metadata
     * @param marker            the group entity marker
     * @return the group query executor
     * @see EntityHandler
     * @see EntityMetadata
     * @see CompositeMetadata
     * @see GroupReferencingEntityMarker
     * @since 1.0.0
     */
    static <K, P extends JsonRecord, R extends UpdatableRecord<R>, D extends VertxDAO<R, P, K>,
               CP extends CompositePojo<P, CP>> GroupQueryExecutor<CP> create(
        @NonNull EntityHandler handler, @NonNull EntityMetadata<K, P, R> metadata,
        @NonNull CompositeMetadata<K, P, R, CP> compositeMetadata, @NonNull GroupReferencingEntityMarker marker) {
        return new GroupDaoQueryExecutor<>(handler, metadata, compositeMetadata, marker);
    }

    /**
     * @return the group reference marker
     * @see GroupReferencingEntityMarker
     * @since 1.0.0
     */
    @Override
    @NonNull GroupReferencingEntityMarker marker();

    /**
     * Verify {@code entity} whether exists or not.
     *
     * @param reqData the request data
     * @return the single
     * @since 1.0.0
     */
    default Future<Boolean> checkReferenceExistence(@NonNull RequestData reqData) {
        return ReferencingQueryExecutor.super.checkReferenceExistence(reqData)
                                             .concatWith(checkGroupExistence(reqData))
                                             .all(aBoolean -> aBoolean);
    }

    default Single<Boolean> checkGroupExistence(@NonNull RequestData reqData) {
        final EntityReferences references = marker().groupReferences();
        final JsonObject body = reqData.body();
        return Observable.fromIterable(references.keys())
                         .flatMapSingle(m -> Optional.ofNullable(body.getJsonObject(m.singularKeyName()))
                                                     .flatMap(b -> Optional.ofNullable(b.getValue(m.jsonKeyName()))
                                                                           .map(Object::toString)
                                                                           .map(m::parseKey))
                                                     .map(k -> fetchExists(queryBuilder().exist(m, k)).switchIfEmpty(
                                                         Single.error(m.notFound(k))))
                                                     .orElseGet(() -> Single.just(true)))
                         .all(aBoolean -> aBoolean);
    }

}
