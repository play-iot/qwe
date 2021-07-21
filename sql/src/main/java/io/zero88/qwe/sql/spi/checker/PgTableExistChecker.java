package io.zero88.qwe.sql.spi.checker;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SQLDialect;
import org.jooq.SelectConditionStep;
import org.jooq.Table;

import lombok.NonNull;

public final class PgTableExistChecker implements TableExistChecker {

    @Override
    public @NonNull SQLDialect dialect() {
        return SQLDialect.POSTGRES;
    }

    @Override
    public <R extends Record> @NonNull SelectConditionStep<Record1<Integer>> query(@NonNull DSLContext dsl,
                                                                                   @NonNull Table<R> table) {
        return dsl.selectCount()
                  .from("pg_tables")
                  .where("schemaname = '" + table.getSchema().getName() + "'")
                  .and("tablename = '" + table.getName() + "'");
    }

}
