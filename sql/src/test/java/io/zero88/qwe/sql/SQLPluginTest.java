package io.zero88.qwe.sql;

import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.zero88.qwe.PluginProvider;
import io.zero88.qwe.PluginTestHelper;
import io.zero88.qwe.PluginTestHelper.PluginDeployTest;
import io.zero88.qwe.sql.handler.EntityHandler;

@SuppressWarnings({"rawtypes", "unchecked"})
@ExtendWith(VertxExtension.class)
public abstract class SQLPluginTest<T extends EntityHandler> implements PluginDeployTest<SQLPlugin> {

    @TempDir
    public Path folder;
    protected T entityHandler;

    @Override
    public Path testDir() {
        return folder;
    }

    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext testContext) {
        entityHandler = (T) ((SQLPluginContext) deploy(vertx, testContext, initConfig(),
                                                       initProvider()).pluginContext()).entityHandler();
    }

    @AfterEach
    public void tearDown(Vertx vertx, VertxTestContext testContext) {
        vertx.close(testContext.succeedingThenComplete());
    }

}
