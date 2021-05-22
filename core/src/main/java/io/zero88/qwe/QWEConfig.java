package io.zero88.qwe;

import java.nio.file.Path;
import java.util.Objects;

import io.github.zero88.utils.FileUtils;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

/**
 * A configuration per each {@code Application}
 *
 * @see Application
 */
@Getter
@Setter(value = AccessLevel.PACKAGE)
@Builder
@Jacksonized
public final class QWEConfig implements IConfig {

    public static final String DATA_DIR = "dataDir";
    public static final Path DEFAULT_DATADIR = FileUtils.defaultDatadir(".playio");

    @JsonProperty(value = QWEConfig.DATA_DIR)
    private Path dataDir;
    @JsonProperty(value = QWEBootConfig.NAME)
    private QWEBootConfig bootConfig;
    @Default
    @JsonProperty(value = QWEDeployConfig.NAME)
    private QWEDeployConfig deployConfig = new QWEDeployConfig();
    @Default
    @JsonProperty(value = QWEAppConfig.NAME)
    private QWEAppConfig appConfig = new QWEAppConfig();

    /**
     * Create default configuration
     *
     * @return QWE configuration
     */
    public static QWEConfig create() { return QWEConfig.create(new JsonObject()); }

    static QWEConfig create(@NonNull JsonObject appConfig) { return create(DEFAULT_DATADIR, appConfig); }

    static QWEConfig create(@NonNull Path dataDir, @NonNull JsonObject appConfig) {
        return QWEConfig.builder().dataDir(dataDir).appConfig(IConfig.from(appConfig, QWEAppConfig.class)).build();
    }

    @Override
    public String key() { return null; }

    @Override
    public Class<? extends IConfig> parent() { return null; }

    public Path dataDir()      { return dataDir = Objects.isNull(dataDir) ? DEFAULT_DATADIR : dataDir; }

    public String getDataDir() { return dataDir().toAbsolutePath().toString(); }

    public static final class QWEDeployConfig extends DeploymentOptions implements IConfig {

        public static final String NAME = "__deploy__";

        @Override
        public String key() { return NAME; }

        @Override
        public Class<? extends IConfig> parent() { return QWEConfig.class; }

    }

}
