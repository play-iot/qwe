package io.zero88.qwe.sql.schema;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Catalog;
import org.jooq.Constraint;
import org.jooq.CreateIndexStep;
import org.jooq.CreateSchemaFinalStep;
import org.jooq.DSLContext;
import org.jooq.ForeignKey;
import org.jooq.Key;
import org.jooq.Schema;
import org.jooq.Table;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zero88.jooqx.SQLExecutor;
import io.zero88.jooqx.SQLPreparedQuery;
import io.zero88.jooqx.SQLResultCollector;
import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.event.EventMessage;
import io.zero88.qwe.sql.handler.EntityHandler;

import lombok.NonNull;

/**
 * Represents for Schema initializer.
 *
 * @since 1.0.0
 */
public interface SchemaInitializer<S, B, PQ extends SQLPreparedQuery<B>, RS, RC extends SQLResultCollector<RS>,
                                      E extends SQLExecutor<S, B, PQ, RS, RC>>
    extends SchemaExecutor<S, B, PQ, RS, RC, E> {

    SchemaInitializer NON_INITIALIZER = entityHandler -> Future.succeededFuture(new JsonObject());

    /**
     * Create {@code Schema}.
     *
     * @param dsl    the dsl
     * @param schema the schema
     * @return the schema for fluent API
     * @since 1.0.0
     */
    default @NonNull Schema createSchema(@NonNull DSLContext dsl, @NonNull Schema schema) {
        try (CreateSchemaFinalStep step = dsl.createSchemaIfNotExists(schema)) {
            logger().debug(step.getSQL());
            step.execute();
            logger().info("SQL::Created schema {} successfully", schema.getName());
            return schema;
        }
    }

    /**
     * Create {@code Table}.
     *
     * @param dsl   the dsl
     * @param table the table
     * @return the table for fluent API
     * @since 1.0.0
     */
    default @NonNull Table<?> createTable(@NonNull DSLContext dsl, @NonNull Table<?> table) {
        logger().info("SQL::Creating table {}...",
                      table.getSchema().getQualifiedName().append(table.getQualifiedName()));
        dsl.createTableIfNotExists(table).columns(table.fields()).execute();
        return table;
    }

    /**
     * Create {@code Table Index} on each {@code Table}.
     *
     * @param dsl   the dsl
     * @param table the table
     * @return the table for fluent API
     * @since 1.0.0
     */
    default @NonNull Table<?> createIndex(@NonNull DSLContext dsl, @NonNull Table<?> table) {
        table.getIndexes().forEach(index -> {
            logger().debug("SQL::Creating index {}...",
                           table.getSchema().getQualifiedName().append(index.getQualifiedName()));
            CreateIndexStep indexStep;
            if (index.getUnique()) {
                indexStep = dsl.createUniqueIndexIfNotExists(index.getName());
            } else {
                indexStep = dsl.createIndexIfNotExists(index.getName());
            }
            indexStep.on(table, index.getFields()).where(index.getWhere()).execute();
        });
        return table;
    }

    default Map<Table, Set<Constraint>> listConstraint(@NonNull Table<?> table) {
        Stream<Constraint> constraints = table.getKeys().stream().map(Key::constraint);
        if (Objects.nonNull(table.getPrimaryKey())) {
            constraints = Stream.concat(constraints, Stream.of(table.getPrimaryKey().constraint()));
        }
        constraints = Stream.concat(constraints, table.getReferences().stream().map(ForeignKey::constraint));
        return Collections.singletonMap(table, constraints.collect(Collectors.toSet()));
    }

    /**
     * Create {@code Constraints}.
     *
     * @param dsl         the dsl
     * @param table       the table
     * @param constraints the constraints
     * @return the dsl context for fluent API
     * @apiNote Creating constraints step is called only once after {@code Table} and {@code Table Index} in {@code
     *     Schema} are created
     * @since 1.0.0
     */
    default @NonNull DSLContext createConstraints(@NonNull DSLContext dsl, @NonNull Table table,
                                                  @NonNull Set<Constraint> constraints) {
        logger().info("SQL::Creating constraints of table {}...", table.getName());
        dsl.setSchema(table.getSchema()).execute();
        constraints.forEach(constraint -> {
            logger().debug("SQL::Constraint: {}", constraint.getQualifiedName());
            dsl.alterTable(table).add(constraint).execute();
        });
        return dsl;
    }

    /**
     * Create {@code Triggers}.
     *
     * @param dsl the dsl
     * @return result of creation
     * @apiNote Creating {@code triggers} step is called only once after {@code Table} and {@code Table Index} in
     *     {@code Schema} are created
     * @since 1.0.0
     */
    default int createTriggers(@NonNull DSLContext dsl) {
        return 0;
    }

    /**
     * Do something with data schema in special case.
     *
     * @param dsl the dsl
     * @return result of stuff
     * @since 1.0.0
     */
    default int doMisc(@NonNull DSLContext dsl) {
        return 0;
    }

    @NonNull Future<JsonObject> initData(@NonNull EntityHandler<S, B, PQ, RS, RC, E> entityHandler);

    @Override
    default Future<EventMessage> execute(@NonNull EntityHandler<S, B, PQ, RS, RC, E> entityHandler) {
        logger().info("SQL::Creating database model...");
        logger().info("SQL::Creating schema...");
        final DSLContext dsl = entityHandler.jooqx().dsl();
        final Catalog catalog = entityHandler.catalog();
        catalog.schemaStream()
               .map(schema -> createSchema(dsl, schema))
               .map(Schema::getTables)
               .flatMap(Collection::stream)
               .map(table -> createTable(dsl, table))
               .map(table -> createIndex(dsl, table))
               .map(this::listConstraint)
               .map(Map::entrySet)
               .flatMap(Collection::stream)
               .collect(Collectors.toMap(Entry::getKey, Entry::getValue,
                                         (c, s) -> Stream.of(c, s).flatMap(Set::stream).collect(Collectors.toSet())))
               .forEach((table, constraints) -> createConstraints(dsl, table, constraints));
        logger().info("SQL::Created {} trigger(s)", this.createTriggers(dsl));
        logger().info("SQL::Done {} SQL stuff(s)", this.doMisc(dsl));
        logger().info("SQL::Created database model successfully");
        return initData(entityHandler).map(body -> EventMessage.success(EventAction.INIT, body));
    }

}
