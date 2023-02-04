package cloud.playio.qwe.sql;

import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import cloud.playio.qwe.BasePluginTest.PluginDeployTest;
import cloud.playio.qwe.PluginDeploymentHelper;
import cloud.playio.qwe.sql.handler.EntityHandler;

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
