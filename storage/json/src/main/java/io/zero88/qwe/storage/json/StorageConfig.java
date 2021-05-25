package io.zero88.qwe.storage.json;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import io.zero88.qwe.QWEAppConfig;
import io.zero88.qwe.IConfig;
import io.zero88.qwe.file.FileOption;
import io.zero88.qwe.storage.json.service.JsonStorageService;
import io.github.zero88.utils.Reflections.ReflectionClass;
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
public final class StorageConfig implements IConfig {

    @Accessors(fluent = true)
    private Path fullPath;
    @Default
    private final String subDir = "storage";
    @Default
    private final int maxSizeInMB = 10;
    @Default
    private final String serviceAddress = "qwe.storage.json";
    @Default
    private final String serviceHandlerClass = JsonStorageService.class.getName();
    /**
     * If {@code chunk} is enabled, it means each key will be persisted in each file that corresponding name
     */
    @Default
    private final boolean chunk = false;
    @Default
    private final FileOption option = FileOption.create();

    public Class<? extends JsonStorageService> serviceHandlerClass() {
        return Optional.ofNullable(ReflectionClass.<JsonStorageService>findClass(serviceHandlerClass))
                       .orElse(JsonStorageService.class);
    }

    @Override
    public String key() {
        return "__json__";
    }

    @Override
    public Class<? extends IConfig> parent() {
        return QWEAppConfig.class;
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
