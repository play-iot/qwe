package cloud.playio.qwe.sql.h2;

import org.jooq.SQLDialect;

import io.vertx.core.json.JsonObject;
import io.github.zero88.jooqx.spi.DBEmbeddedMode;
import cloud.playio.qwe.sql.SQLPluginConfig;

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
