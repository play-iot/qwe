package io.github.zero88.storage.json;

import java.nio.file.Path;
import java.nio.file.Paths;

import io.github.zero88.qwe.CarlConfig.AppConfig;
import io.github.zero88.qwe.IConfig;
import io.github.zero88.qwe.file.FileOption;
import io.github.zero88.utils.Strings;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

@Getter
@Jacksonized
@Builder(builderClassName = "Builder")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StorageConfig implements IConfig {

    @Accessors(fluent = true)
    private Path fullPath;
    @Default
    private final String subDir = "storage";
    @Default
    private final int maxSizeInMB = 10;
    /**
     * If {@code chunk} is enabled, it means each key will be persisted in each file that corresponding name
     */
    @Default
    private final boolean chunk = true;
    @Default
    private final FileOption option = FileOption.create();

    @Override
    public String key() {
        return "__json__";
    }

    @Override
    public Class<? extends IConfig> parent() {
        return AppConfig.class;
    }

    public StorageConfig makeFullPath(@NonNull String rootDir) {
        return makeFullPath(Paths.get(rootDir));
    }

    public StorageConfig makeFullPath(@NonNull Path rootDir) {
        fullPath = (Strings.isBlank(subDir) ? rootDir : rootDir.resolve(subDir)).toAbsolutePath();
        return this;
    }

    static StorageConfig create() {
        return StorageConfig.builder().build();
    }

    public static class Builder {

        private Builder fullPath(Path fullPath) { return this; }

    }

}
