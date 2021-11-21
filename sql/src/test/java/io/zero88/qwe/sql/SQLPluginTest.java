package io.zero88.qwe.sql;

import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.zero88.qwe.BasePluginTest.PluginDeployTest;
import io.zero88.qwe.PluginDeploymentHelper;
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
        final SQLPlugin plugin = PluginDeploymentHelper.Junit5.create(this)
                                                              .deploy(vertx, testContext, initConfig(), initProvider());
        entityHandler = (T) ((SQLPluginContext) plugin.pluginContext()).entityHandler();
    }

    @AfterEach
    public void tearDown(Vertx vertx, VertxTestContext testContext) {
        vertx.close(testContext.succeedingThenComplete());
    }

}
