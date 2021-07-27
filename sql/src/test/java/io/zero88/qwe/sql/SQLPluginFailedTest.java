package io.zero88.qwe.sql;

import java.nio.file.Path;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import io.vertx.junit5.VertxExtension;
import io.zero88.qwe.PluginTestHelper;

@ExtendWith(VertxExtension.class)
public abstract class SQLPluginFailedTest implements PluginTestHelper {

    @TempDir
    public Path folder;

    @Override
    public Path testDir() {
        return folder;
    }

}
