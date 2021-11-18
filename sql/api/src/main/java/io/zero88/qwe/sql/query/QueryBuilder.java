package io.zero88.qwe.sql.query;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.JoinType;
import org.jooq.OrderField;
import org.jooq.Record;
import org.jooq.ResultQuery;
import org.jooq.SelectConditionStep;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.SelectJoinStep;
import org.jooq.SelectLimitStep;
import org.jooq.SelectOptionStep;
import org.jooq.SelectSeekStepN;
import org.jooq.Table;
import org.jooq.impl.DSL;

import io.github.zero88.jpa.Sortable.Direction;
import io.zero88.qwe.sql.EntityMetadata;
import io.zero88.qwe.sql.tables.JsonTable;
import io.zero88.qwe.dto.jpa.Pagination;
import io.zero88.qwe.dto.jpa.Sort;
import io.zero88.qwe.dto.msg.RequestFilter;
import io.github.zero88.rql.jooq.JooqFieldMapper;
import io.github.zero88.rql.jooq.JooqQueryContext;
import io.github.zero88.rql.jooq.JooqRqlParser;
import io.github.zero88.rql.jooq.visitor.JooqConditionRqlVisitor;
import io.github.zero88.utils.Strings;
import io.vertx.core.json.JsonObject;

import lombok.NonNull;

/**
 * Represents Query builder.
 *
 * @see EntityMetadata
 * @since 1.0.0
 */
//TODO join part is still in beta mode, with limited supported for many joins
public final class QueryBuilder {

    private final EntityMetadata base;
    private final Map<EntityMetadata, Condition> joinBy = new HashMap<>();
    private final List<OrderField<?>> orderFields;
    private Collection<EntityMetadata> references;
    private Predicate<EntityMetadata> predicate = metadata -> true;
    private JoinType joinType = JoinType.JOIN;
    private Supplier<List<SelectFieldOrAsterisk>> fields = () -> Collections.singletonList(DSL.asterisk());

    /**
     * Instantiates a new Query builder.
     *
     * @param base the base entity metadata
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    public QueryBuilder(@NonNull EntityMetadata base) {
        this.base = base;
        this.orderFields = base.orderFields();
    }

    /**
     * Add references.
     *
     * @param references the metadata references
     * @return a reference to this, so the API can be used fluently
     * @since 1.0.0
     */
    public QueryBuilder references(@NonNull Collection<EntityMetadata> references) {
        this.references = references;
        return this;
    }

    /**
     * Add entity metadata predicate to include/exclude in build {@code SQL query} in view/checking existence.
     *
     * @param predicate the predicate
     * @return a reference to this, so the API can be used fluently
     * @since 1.0.0
     */
    public QueryBuilder predicate(@NonNull Predicate<EntityMetadata> predicate) {
        this.predicate = predicate;
        return this;
    }

    /**
     * Add global {@code Join type}.
     *
     * @param type the type
     * @return a reference to this, so the API can be used fluently
     * @see JoinType
     * @since 1.0.0
     */
    public QueryBuilder joinType(@NonNull JoinType type) {
        this.joinType = type;
        return this;
    }

    /**
     * Add join by {@code condition} depends on particular {@code entity metadata}.
     *
     * @param metadata  the metadata
     * @param condition the condition
     * @return a reference to this, so the API can be used fluently
     * @see Condition
     * @since 1.0.0
     */
    public QueryBuilder joinBy(@NonNull EntityMetadata metadata, @NonNull Condition condition) {
        joinBy.put(metadata, condition);
        return this;
    }

    /**
     * Add join fields. Default is {@code asterisk}
     *
     * @param selectFields the select fields
     * @return a reference to this, so the API can be used fluently
     * @see SelectFieldOrAsterisk
     * @since 1.0.0
     */
    public QueryBuilder joinFields(@NonNull Supplier<List<SelectFieldOrAsterisk>> selectFields) {
        this.fields = selectFields;
        return this;
    }

    /**
     * Create view query
     *
     * @param filter     Request filter
     * @param sort       Sort
     * @param pagination pagination
     * @return query function
     * @see Sort
     * @see Pagination
     * @see RequestFilter
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    public Function<DSLContext, ? extends ResultQuery<? extends Record>> view(RequestFilter filter, Sort sort,
                                                                              Pagination pagination) {
        final @NonNull JsonTable<? extends Record> table = base.table();
        return context -> {
            final SelectJoinStep<Record> query = context.select(fields.get()).from(table);
            if (Objects.nonNull(references)) {
                references.stream()
                          .filter(predicate)
                          .forEach(meta -> doJoin(query, meta, QueryParser.fromReference(meta, filter), joinType));
            }
            return (ResultQuery<? extends Record>) paging(orderBy(query.where(condition(table, filter, false)), sort),
                                                          pagination);
        };
    }

    /**
     * Create view query for one resource
     *
     * @param filter Request filter
     * @param sort   Sort
     * @return query function
     * @see RequestFilter
     * @see Sort
     * @since 1.0.0
     */
    public Function<DSLContext, ? extends ResultQuery<? extends Record>> viewOne(RequestFilter filter, Sort sort) {
        return view(filter, sort, Pagination.oneValue());
    }

    /**
     * Create Exist function by {@code primary key}
     *
     * @param metadata the metadata
     * @param key      the primary key
     * @return the function
     * @since 1.0.0
     */
    public Function<DSLContext, Boolean> exist(@NonNull EntityMetadata metadata, @NonNull Object key) {
        return exist(metadata.table(), conditionByPrimary(metadata, key));
    }

    /**
     * Create Exist function by {@code filter}
     *
     * @param metadata the metadata
     * @param filter   the filter
     * @return the function
     * @since 1.0.0
     */
    public Function<DSLContext, Boolean> exist(@NonNull EntityMetadata metadata, @NonNull RequestFilter filter) {
        return dsl -> dsl.fetchExists(metadata.table(), condition(metadata, filter));
    }

    /**
     * Create Exist function by join with filter.
     *
     * @param filter the filter
     * @return the function
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    public Function<DSLContext, ? extends ResultQuery<? extends Record>> existQueryByJoin(
        @NonNull RequestFilter filter) {
        final @NonNull JsonTable<? extends Record> table = base.table();
        final RequestFilter nullable = new RequestFilter();
        return context -> {
            final SelectJoinStep<Record> query = context.select(onlyPrimaryKeys()).from(table);
            if (Objects.nonNull(references)) {
                references.stream()
                          .peek(meta -> {
                              if (!predicate.test(meta)) {
                                  nullable.put(meta.requestKeyName(), filter.getValue(meta.requestKeyName()));
                              }
                          })
                          .filter(predicate)
                          .forEach(meta -> doJoin(query, meta, new RequestFilter().put(meta.jsonKeyName(),
                                                                                       filter.getValue(
                                                                                           meta.requestKeyName())),
                                                  JoinType.RIGHT_OUTER_JOIN));
            }
            return (ResultQuery<? extends Record>) query.where(condition(table, nullable, true)).limit(1);
        };
    }

    /**
     * Create database condition by request filter
     * <p>
     * It is simple filter function by equal comparision. Any complex query should be override by each service.
     *
     * @param metadata Entity metadata
     * @param filter   Filter request
     * @return Database Select DSL
     * @see Condition
     * @since 1.0.0
     */
    public Condition condition(@NonNull EntityMetadata metadata, RequestFilter filter) {
        return condition(metadata, filter, false);
    }

    /**
     * Condition condition.
     *
     * @param metadata      the metadata
     * @param filter        the filter
     * @param allowNullable the allow nullable
     * @return the condition
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    public Condition condition(@NonNull EntityMetadata metadata, RequestFilter filter, boolean allowNullable) {
        return condition(metadata.table(), filter, allowNullable);
    }

    Function<DSLContext, Boolean> exist(@NonNull Table table, @NonNull Condition condition) {
        return dsl -> dsl.fetchExists(table, condition);
    }

    @SuppressWarnings("unchecked")
    Condition conditionByPrimary(@NonNull EntityMetadata metadata, @NonNull Object key) {
        return metadata.table().getField(metadata.jsonKeyName()).eq(key);
    }

    @SuppressWarnings("unchecked")
    private Condition condition(@NonNull JsonTable<? extends Record> table, RequestFilter filter,
                                boolean allowNullable) {
        if (Objects.isNull(filter)) {
            return DSL.trueCondition();
        }
        final Condition[] c = new Condition[] {DSL.trueCondition()};
        filter.streamExtraFilter().map(entry -> {
            final Field field = table.getField(entry.getKey());
            return Optional.ofNullable(field)
                           .map(f -> Optional.ofNullable(entry.getValue())
                                             .map(v -> allowNullable ? f.eq(v).or(f.isNull()) : f.eq(v))
                                             .orElseGet(f::isNull))
                           .orElse(null);
        }).filter(Objects::nonNull).forEach(condition -> c[0] = c[0].and(condition));
        return c[0].and(advanceQuery(table, filter.advanceQuery()));
    }

    private Condition advanceQuery(@NonNull JsonTable<? extends Record> table, String advanceQuery) {
        if (Strings.isBlank(advanceQuery)) {
            return DSL.trueCondition();
        }
        return JooqRqlParser.DEFAULT.criteria(advanceQuery,
                                              JooqConditionRqlVisitor.create(table, new JooqQueryContext() {
                                                  @Override
                                                  public @NonNull JooqFieldMapper fieldMapper() {
                                                      return JooqFieldMapper.SNAKE_UPPERCASE_MAPPER;
                                                  }
                                              }));
    }

    private SelectSeekStepN<? extends Record> orderBy(@NonNull SelectConditionStep<? extends Record> sql, Sort sort) {
        if (Objects.isNull(sort) || sort.isEmpty()) {
            return sql.orderBy(orderFields);
        }

        final JsonObject jsonSort = sort.toJson();
        final Stream<OrderField<?>> sortFields = Stream.concat(
            jsonSort.stream().filter(entry -> !entry.getKey().contains(".")).map(entry -> sortField(base, entry)),
            Optional.ofNullable(references)
                    .map(refs -> refs.stream()
                                     .flatMap(
                                         meta -> QueryParser.streamRefs(meta, jsonSort).map(e -> sortField(meta, e))))
                    .orElse(Stream.empty()));
        return sql.orderBy(sortFields.filter(Objects::nonNull).toArray(OrderField[]::new));
    }

    private OrderField<?> sortField(@NonNull EntityMetadata meta, @NonNull Entry<String, Object> entry) {
        final Direction type = Direction.parse(Strings.toString(entry.getValue()));
        if (type == null) {
            return null;
        }
        final Field<?> field = meta.table().getField(entry.getKey());
        return Optional.ofNullable(field).map(f -> Direction.DESC == type ? f.desc() : f.asc()).orElse(null);
    }

    /**
     * Do query paging
     *
     * @param sql        SQL select command
     * @param pagination Given pagination
     * @return Database Select DSL
     */
    private SelectOptionStep<? extends Record> paging(@NonNull SelectLimitStep<? extends Record> sql,
                                                      Pagination pagination) {
        Pagination paging = Optional.ofNullable(pagination).orElseGet(() -> Pagination.builder().build());
        return sql.limit(paging.getPerPage()).offset((paging.getPage() - 1) * paging.getPerPage());
    }

    private List<SelectFieldOrAsterisk> onlyPrimaryKeys() {
        return Stream.concat(Stream.of(base.table().asterisk()), Optional.ofNullable(references)
                                                                         .map(s -> s.stream()
                                                                                    .filter(predicate)
                                                                                    .map(entry -> entry.table()
                                                                                                       .getPrimaryKey()
                                                                                                       .getFieldsArray())
                                                                                    .flatMap(Stream::of))
                                                                         .orElse(Stream.empty()))
                     .collect(Collectors.toList());
    }

    private void doJoin(SelectJoinStep<Record> query, EntityMetadata meta, RequestFilter filter, JoinType joinType) {
        final Condition joinCondition = joinBy.get(meta);
        if (Objects.isNull(joinCondition)) {
            query.join(meta.table(), joinType).onKey().where(condition(meta, filter));
            return;
        }
        query.join(meta.table(), joinType).on(joinCondition).where(condition(meta, filter));
    }

}
