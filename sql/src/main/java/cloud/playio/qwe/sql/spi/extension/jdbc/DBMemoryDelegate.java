package cloud.playio.qwe.sql.spi.extension.jdbc;

import org.jooq.SQLDialect;

import io.github.zero88.jooqx.provider.DBEmbeddedProvider.DBMemoryProvider;
import io.github.zero88.jooqx.spi.derby.DerbyMemProvider;
import io.github.zero88.jooqx.spi.h2.H2MemProvider;
import io.github.zero88.jooqx.spi.hsqldb.HSQLDBMemProvider;
import io.github.zero88.jooqx.spi.sqlite.SQLiteMemProvider;
import cloud.playio.qwe.exceptions.InitializerError;

public class DBMemoryDelegate extends DBEmbeddedDelegate<DBMemoryProvider> implements DBMemoryProvider {

    public DBMemoryDelegate(SQLDialect dialect) {
        super(init(dialect));
    }

    static DBMemoryProvider init(SQLDialect dialect) {
        if (SQLDialect.DERBY == dialect) {
            return new DerbyMemProvider() {};
        }
        if (SQLDialect.H2 == dialect) {
            return new H2MemProvider() {};
        }
        if (SQLDialect.HSQLDB == dialect) {
            return new HSQLDBMemProvider() {};
        }
        if (SQLDialect.SQLITE == dialect) {
            return new SQLiteMemProvider() {};
        }
        throw new InitializerError("Unsupported Database in memory for Dialect [" + dialect + "]");
    }

}
