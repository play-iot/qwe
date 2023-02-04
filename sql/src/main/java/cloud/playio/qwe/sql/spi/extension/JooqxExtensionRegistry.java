package cloud.playio.qwe.sql.spi.extension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jooq.SQLDialect;

import io.github.zero88.repl.ReflectionClass;
import io.vertx.ext.jdbc.spi.DataSourceProvider;
import io.github.zero88.jooqx.provider.JDBCExtension;
import io.github.zero88.jooqx.spi.jdbc.JDBCPoolProvider;
import cloud.playio.qwe.sql.handler.JooqxBaseExtension;
import cloud.playio.qwe.sql.spi.extension.db2.DB2JooqxExtension;
import cloud.playio.qwe.sql.spi.extension.jdbc.JDBCPoolAgroalJooqxExtension;
import cloud.playio.qwe.sql.spi.extension.jdbc.JDBCPoolC3P0JooqxExtension;
import cloud.playio.qwe.sql.spi.extension.jdbc.JDBCPoolHikariJooqxExtension;
import cloud.playio.qwe.sql.spi.extension.jdbc.JDBCPoolJooqxExtension;
import cloud.playio.qwe.sql.spi.extension.mssql.MSSQLJooqxExtension;
import cloud.playio.qwe.sql.spi.extension.mysql.MySQLJooqxExtension;
import cloud.playio.qwe.sql.spi.extension.pg.PgJooqxExtension;

public final class JooqxExtensionRegistry {

    private static final Map<String, JooqxBaseExtension> EXTENSIONS = init();
    private static final List<JooqxBaseExtension> JDBC_EXT = Arrays.asList(JDBCPoolHikariJooqxExtension.INSTANCE,
                                                                           JDBCPoolAgroalJooqxExtension.INSTANCE,
                                                                           JDBCPoolC3P0JooqxExtension.INSTANCE);

    static Map<String, JooqxBaseExtension> init() {
        final Map<String, JooqxBaseExtension> m = new HashMap<>();
        m.put(SQLDialect.MARIADB.getNameUC(), MySQLJooqxExtension.INSTANCE);
        m.put(SQLDialect.MYSQL.getNameUC(), MySQLJooqxExtension.INSTANCE);
        m.put(SQLDialect.POSTGRES.getNameUC(), PgJooqxExtension.INSTANCE);
        m.put("DB2", DB2JooqxExtension.INSTANCE);
        m.put("MSSQL", MSSQLJooqxExtension.INSTANCE);
        return m;
    }

    public static JooqxBaseExtension lookup(SQLDialect dialect) {
        final JooqxBaseExtension jooqxExtension = EXTENSIONS.get(dialect.getNameUC());
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
