package io.zero88.qwe.sql.spi.extension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jooq.SQLDialect;

import io.github.zero88.repl.ReflectionClass;
import io.vertx.ext.jdbc.spi.DataSourceProvider;
import io.zero88.jooqx.provider.JDBCExtension;
import io.zero88.jooqx.spi.jdbc.JDBCPoolProvider;
import io.zero88.qwe.sql.handler.JooqxExtension;
import io.zero88.qwe.sql.spi.extension.db2.DB2JooqxReactiveExtension;
import io.zero88.qwe.sql.spi.extension.jdbc.JDBCPoolAgroalJooqxExtension;
import io.zero88.qwe.sql.spi.extension.jdbc.JDBCPoolC3P0JooqxExtension;
import io.zero88.qwe.sql.spi.extension.jdbc.JDBCPoolHikariJooqxExtension;
import io.zero88.qwe.sql.spi.extension.jdbc.JDBCPoolJooqxExtension;
import io.zero88.qwe.sql.spi.extension.mssql.MSSQLJooqxReactiveExtension;
import io.zero88.qwe.sql.spi.extension.mysql.MySQLJooqxReactiveExtension;
import io.zero88.qwe.sql.spi.extension.pg.PgJooqxReactiveExtension;

public final class JooqxExtensionRegistry {

    private static final Map<String, JooqxExtension> EXTENSIONS = init();
    private static final List<JooqxExtension> JDBC_EXT = Arrays.asList(JDBCPoolHikariJooqxExtension.INSTANCE,
                                                                       JDBCPoolAgroalJooqxExtension.INSTANCE,
                                                                       JDBCPoolC3P0JooqxExtension.INSTANCE);

    static Map<String, JooqxExtension> init() {
        final Map<String, JooqxExtension> m = new HashMap<>();
        m.put(SQLDialect.MARIADB.getNameUC(), MySQLJooqxReactiveExtension.INSTANCE);
        m.put(SQLDialect.MYSQL.getNameUC(), MySQLJooqxReactiveExtension.INSTANCE);
        m.put(SQLDialect.POSTGRES.getNameUC(), PgJooqxReactiveExtension.INSTANCE);
        m.put("DB2", DB2JooqxReactiveExtension.INSTANCE);
        m.put("MSSQL", MSSQLJooqxReactiveExtension.INSTANCE);
        return m;
    }

    public static JooqxExtension lookup(SQLDialect dialect) {
        final JooqxExtension jooqxExtension = EXTENSIONS.get(dialect.getNameUC());
        if (jooqxExtension != null && ReflectionClass.hasClass(jooqxExtension.clientProvider().sqlClientClass())) {
            return jooqxExtension;
        }
        return JDBC_EXT.stream()
                       .filter(ext -> ReflectionClass.hasClass(ext.sqlClientClass()) &&
                                      ReflectionClass.hasClass(((JDBCExtension) ext).jdbcDataSourceClass()))
                       .findFirst()
                       .map(ext -> new JDBCPoolJooqxExtension<>((JDBCPoolProvider<? extends DataSourceProvider>) ext))
                       .orElse(null);
    }

}
