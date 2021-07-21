package io.zero88.qwe.sql.spi.checker;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SQLDialect;
import org.jooq.SelectConditionStep;
import org.jooq.Table;

import lombok.NonNull;

/**
 * A factory for the plug-able {@code Table exist} checker on per a specific database.
 *
 * <ul>
 * <li>An attempt is made to load a factory using the service loader {@code META-INF/services}
 * {@link TableExistChecker}</li>
 * <li>Factories are sorted via {@link #dialect()} and {@link #order()}</li>
 * </ul>
 */
public interface TableExistChecker {

    /**
     * Defines SQL dialect for which database
     *
     * @return dialect
     */
    @NonNull SQLDialect dialect();

    /**
     * The order of the checker.
     * <p>
     * If there is more than one matching checker with same {@code SQLDialect}, they will be tried in descending order.
     *
     * @return the order
     * @implSpec The default value is 0. Then if want to override, returns any value is greater than 0.
     */
    default int order() {
        return 0;
    }

    /**
     * Build a query to check whether table exists or not
     *
     * @param dsl   dsl context
     * @param table table
     * @param <R>   Type of Record
     * @return a query
     */
    @NonNull <R extends Record> SelectConditionStep<Record1<Integer>> query(@NonNull DSLContext dsl,
                                                                            @NonNull Table<R> table);

}
