package io.zero88.qwe.sql.h2;

import org.jooq.SQLDialect;

import io.vertx.core.json.JsonObject;
import io.zero88.jooqx.spi.DBEmbeddedMode;
import io.zero88.qwe.sql.SQLPluginConfig;

public class H2FileAutoDetectTest extends H2TestBase {

    @Override
    public SQLPluginConfig initConfig() {
        return new SQLPluginConfig().setPluginDir("")
                                    .setEmbeddedMode(DBEmbeddedMode.FILE)
                                    .setDialect(SQLDialect.H2)
                                    .setAutoDetect(true)
                                    .connectionOptions(new JsonObject().put("maximumPoolSize", 3));
    }

}
