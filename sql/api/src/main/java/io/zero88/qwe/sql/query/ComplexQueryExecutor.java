package cloud.playio.qwe.sql.query;

import java.util.function.Predicate;

import io.github.jklingsporn.vertx.jooq.rx.RXQueryExecutor;
import io.github.jklingsporn.vertx.jooq.shared.internal.jdbc.JDBCQueryExecutor;
import cloud.playio.qwe.sql.CompositeMetadata;
import cloud.playio.qwe.sql.EntityMetadata;
import cloud.playio.qwe.sql.pojos.CompositePojo;
import cloud.playio.qwe.sql.handler.EntityHandler;
import cloud.playio.qwe.sql.marker.EntityReferences;
import io.reactivex.Single;


import lombok.NonNull;

/**
 * Represents an executor that do complex query with {@code join} or {@code group by}
 *
 * @param <CP> Type of {@code CompositePojo}
 * @see ReferencingQueryExecutor
 * @see JDBCQueryExecutor
 * @see RXQueryExecutor
 * @since 1.0.0
 */
public interface ComplexQueryExecutor<CP extends CompositePojo>
    extends ReferencingQueryExecutor<CP>, JDBCQueryExecutor<Single<?>>, RXQueryExecutor {

    /**
     * Create complex query executor.
     *
     * @param handler the handler
     * @return the complex query executor
     * @since 1.0.0
     */
    static ComplexQueryExecutor create(@NonNull EntityHandler handler) {
        return new ComplexDaoQueryExecutor(handler);
    }

    /**
     * Defines {@code context metadata}
     *
     * @param metadata context metadata
     * @return a reference to this, so the API can be used fluently
     * @since 1.0.0
     */
    ComplexQueryExecutor from(@NonNull CompositeMetadata metadata);

    /**
     * Defines {@code resource metadata} as presenter
     *
     * @param resourceMetadata resource metadata
     * @return a reference to this, so the API can be used fluently
     * @since 1.0.0
     */
    ComplexQueryExecutor with(@NonNull EntityMetadata resourceMetadata);

    /**
     * Add {@code context metadata}
     *
     * @param contextMetadata context metadata
     * @return a reference to this, so the API can be used fluently
     * @since 1.0.0
     */
    ComplexQueryExecutor context(@NonNull EntityMetadata contextMetadata);

    /**
     * Add {@code entity references}.
     *
     * @param references the references
     * @return a reference to this, so the API can be used fluently
     * @since 1.0.0
     */
    ComplexQueryExecutor references(@NonNull EntityReferences references);

    /**
     * Add {@code view predicate}.
     *
     * @param predicate the predicate
     * @return a reference to this, so the API can be used fluently
     * @since 1.0.0
     */
    ComplexQueryExecutor viewPredicate(@NonNull Predicate<EntityMetadata> predicate);

}
