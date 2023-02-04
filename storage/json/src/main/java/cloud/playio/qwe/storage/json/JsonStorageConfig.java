package cloud.playio.qwe.storage.json;

import java.util.Optional;

import io.github.zero88.repl.ReflectionClass;
import cloud.playio.qwe.ExtensionConfig;
import cloud.playio.qwe.file.FileOption;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Getter
@Jacksonized
@Builder(builderClassName = "Builder")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class JsonStorageConfig implements ExtensionConfig {

    public static final String KEY = "__json__";
    @Default
    private final String pluginDir = "storage";
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
    public String configKey() {
        return KEY;
    }

    public static JsonStorageConfig create() {
        return JsonStorageConfig.builder().build();
    }

}
