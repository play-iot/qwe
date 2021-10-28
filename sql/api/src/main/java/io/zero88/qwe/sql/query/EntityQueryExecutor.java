package io.zero88.qwe.sql.query;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.jooq.UpdatableRecord;
import org.jooq.exception.TooManyRowsException;

import io.github.zero88.exceptions.HiddenException;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Future;
import io.zero88.jooqx.JsonRecord;
import io.zero88.qwe.dto.jpa.Pagination;
import io.zero88.qwe.dto.jpa.Sort;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.dto.msg.RequestFilter;
import io.zero88.qwe.exceptions.ImplementationError;
import io.zero88.qwe.exceptions.QWEException;
import io.zero88.qwe.sql.EntityMetadata;
import io.zero88.qwe.sql.exceptions.DatabaseException;
import io.zero88.qwe.sql.EntityConstraintHolder;
import io.zero88.qwe.sql.handler.EntityHandler;
import io.zero88.qwe.sql.pojos.DMLPojo;
import io.zero88.qwe.sql.validation.OperationValidator;
import io.zero88.qwe.utils.JsonUtils;

import lombok.NonNull;

/**
 * Represents for a {@code SQL  Executor} do {@code DML} or {@code DQL} on {@code entity}.
 *
 * @param <P> Type of {@code JsonRecord}
 * @since 1.0.0
 */
//TODO lack unique keys validation. https://github.com/NubeIO/iot-engine/issues/321
public interface EntityQueryExecutor<P extends JsonRecord> {

    /**
     * Sneaky throw database error in single type.
     *
     * @param throwable the throwable
     * @return error single
     * @since 1.0.0
     */
    static <T> @NonNull Future<T> sneakyThrowDBError(@NonNull Throwable throwable) {
        if (throwable instanceof TooManyRowsException) {
            return Future.failedFuture(new ImplementationError(DatabaseException.CODE,
                                                               "Query is not correct, the result contains more than " +
                                                               "one " + "record", throwable));
        }
        return Future.failedFuture(throwable);
    }

    /**
     * Sneaky throw database error in case of {@code unable delete} entity.
     *
     * @param clue the clue
     * @return error single
     * @since 1.0.0
     */
    static <T> @NonNull Future<T> unableDelete(String clue) {
        return Future.failedFuture(
            new QWEException("Cannot delete record", new HiddenException(DatabaseException.CODE, clue, null)));
    }

    /**
     * Declares entity handler.
     *
     * @return the entity handler
     * @see EntityHandler
     * @since 1.0.0
     */
    @NonNull EntityHandler entityHandler();

    @NonNull Configuration runtimeConfiguration();

    @NonNull EntityQueryExecutor runtimeConfiguration(Configuration configuration);

    /**
     * Declares query builder.
     *
     * @return the query builder
     * @see QueryBuilder
     * @since 1.0.0
     */
    @NonNull QueryBuilder queryBuilder();

    /**
     * Create {@code DAO} based on given {@code dao class}.
     *
     * @param <K>      Type of {@code primary key}
     * @param <R>      Type of {@code UpdatableRecord}
     * @param <D>      Type of {@code VertxDAO}
     * @param metadata the entity metadata
     * @return instance of {@code DAO}
     * @since 1.0.0
     */
    default <K, R extends UpdatableRecord<R>, D extends VertxDAO<R, P, K>> D dao(EntityMetadata<K, P, R> metadata) {
        return entityHandler().dao(runtimeConfiguration(), metadata.daoClass());
    }

    /**
     * Find many entity resources.
     *
     * @param requestData Request data
     * @return list pojo entities
     * @since 1.0.0
     */
    @NonNull Future<List<P>> findMany(@NonNull RequestData requestData);

    /**
     * Find one resource by {@code primary key} or by {@code composite unique key} after analyzing given request data
     *
     * @param requestData Request data
     * @return single pojo
     * @since 1.0.0
     */
    @NonNull Future<P> findOneByKey(@NonNull RequestData requestData);

    /**
     * Get one resource by {@code primary key}
     *
     * @param primaryKey Given primary key
     * @return one single data source if found else throw {@code not found exception}
     * @see EntityMetadata#notFound(Object)
     * @since 1.0.0
     */
    @NonNull Future<P> lookupByPrimaryKey(@NonNull Object primaryKey);

    /**
     * Create new resource then return {@code primary key}
     *
     * @param requestData request data
     * @param validator   creation validator
     * @return DML pojo
     * @see DMLPojo
     * @since 1.0.0
     */
    @NonNull Future<DMLPojo> insertReturningPrimary(@NonNull RequestData requestData,
                                                    @NonNull OperationValidator validator);

    /**
     * Do update data on both {@code UPDATE} or {@code PATCH} action
     *
     * @param requestData Request data
     * @param validator   modification validator
     * @return DML pojo
     * @see DMLPojo
     * @since 1.0.0
     */
    @NonNull Future<DMLPojo> modifyReturningPrimary(@NonNull RequestData requestData,
                                                    @NonNull OperationValidator validator);

    /**
     * Do delete data by {@code primary key}
     *
     * @param requestData Request data
     * @param validator   deletion validator
     * @return deleted resource
     * @since 1.0.0
     */
    @NonNull Future<P> deleteOneByKey(@NonNull RequestData requestData, @NonNull OperationValidator validator);

    /**
     * Execute any function
     *
     * @param <X>      Type of {@code result}
     * @param function query function
     * @return result single
     * @apiNote Only using it in very complex case or special case
     * @see QueryBuilder#view(RequestFilter, Sort, Pagination)
     * @see QueryBuilder#viewOne(RequestFilter, Sort)
     * @see QueryBuilder#exist(Table, Condition)
     * @since 1.0.0
     */
    @NonNull <X> Future<X> executeAny(@NonNull Function<DSLContext, X> function);

    /**
     * Check whether resource is existed or not
     *
     * @param query Given query
     * @return empty if resource is not existed or {@code true}
     * @see QueryBuilder#exist(Table, Condition)
     * @since 1.0.0
     */
    default @NonNull Future<@Nullable Boolean> fetchExists(@NonNull Function<DSLContext, Boolean> query) {
        return executeAny(query).map(Objects::nonNull);
    }

    default @NonNull Future<@Nullable Boolean> fetchExists(@NonNull Table table, @NonNull Condition condition) {
        return fetchExists(queryBuilder().exist(table, condition));
    }

    /**
     * Check resource is able to delete by scanning reference resource to this resource
     *
     * @param pojo     Resource
     * @param metadata Entity metadata
     * @return single pojo or single existed error
     * @see EntityMetadata#unableDeleteDueUsing(String)
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    default @NonNull Future<Boolean> isAbleToDelete(@NonNull P pojo, @NonNull EntityMetadata metadata) {
        final EntityConstraintHolder holder = entityHandler().holder();
        if (EntityConstraintHolder.BLANK == holder) {
            return Future.succeededFuture(true);
        }
        final Object pk = metadata.parseKey(pojo);
        return Future.succeededFuture(holder.referenceTo(metadata.table()))
                     .flatMap(ref -> fetchExists(queryBuilder().exist(ref.getTable(), ref.getField().eq(pk))))
                     .flatMap(b -> Future.failedFuture(
                         metadata.unableDeleteDueUsing(requestKeyAsMessage(metadata, pojo, pk))))
                     .map(Boolean.class::cast)
                     .defaultIfEmpty(true)
                     .singleOrError();
    }

    default @NonNull String requestKeyAsMessage(@NonNull EntityMetadata metadata, @NonNull JsonRecord pojo,
                                                @NonNull Object primaryKey) {
        return JsonUtils.kvMsg(metadata.requestKeyName(), primaryKey);
    }

}
