package cloud.playio.qwe.sql.spi.extension.jdbc;

import org.jooq.SQLDialect;

import io.github.zero88.utils.Strings;
import io.github.zero88.utils.UUID64;
import io.vertx.core.json.JsonObject;
import io.github.zero88.jooqx.provider.DBEmbeddedProvider.DBFileProvider;
import io.github.zero88.jooqx.spi.derby.DerbyFileProvider;
import io.github.zero88.jooqx.spi.h2.H2FileProvider;
import io.github.zero88.jooqx.spi.hsqldb.HSQLDBFileProvider;
import io.github.zero88.jooqx.spi.sqlite.SQLiteFileProvider;
import cloud.playio.qwe.HasLogger;
import cloud.playio.qwe.exceptions.InitializerError;

public class DBFileDelegate extends DBEmbeddedDelegate<DBFileProvider> implements DBFileProvider, HasLogger {

    public DBFileDelegate(String databaseName, SQLDialect dialect) {
        super(init(databaseName, dialect));
    }

    static DBFileProvider init(String databaseName, SQLDialect dialect) {
        if (SQLDialect.DERBY == dialect) {
            return (DerbyFileProvider) () -> databaseName;
        }
        if (SQLDialect.H2 == dialect) {
            return (H2FileProvider) () -> databaseName;
        }
        if (SQLDialect.HSQLDB == dialect) {
            return (HSQLDBFileProvider) () -> databaseName;
        }
        if (SQLDialect.SQLITE == dialect) {
            return (SQLiteFileProvider) () -> databaseName;
        }
        throw new InitializerError("Unsupported Database in file for Dialect [" + dialect + "]");
    }

    @Override
    public String user(JsonObject connOptions) {
        String user = connOptions.getString("username", connOptions.getString("user"));
        if (Strings.isBlank(user)) {
            user = "admin";
        }
        logger().info("Database user [{}]", user);
        return user;
    }

    @Override
    public String password(JsonObject connOptions) {
        String password = connOptions.getString("password");
        if (Strings.isBlank(password)) {
            password = UUID64.random();
            logger().info("Database password [{}]", password);
        }
        return password;
    }

}
