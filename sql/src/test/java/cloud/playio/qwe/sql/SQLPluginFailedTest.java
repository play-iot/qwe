package cloud.playio.qwe.sql;

import java.nio.file.Path;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import io.vertx.junit5.VertxExtension;
import cloud.playio.qwe.BasePluginTest;

@ExtendWith(VertxExtension.class)
public abstract class SQLPluginFailedTest implements BasePluginTest {

    @TempDir
    public Path folder;

    @Override
    public Path testDir() {
        return folder;
    }

}
