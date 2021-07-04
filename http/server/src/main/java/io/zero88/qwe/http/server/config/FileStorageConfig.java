package io.zero88.qwe.http.server.config;

import io.zero88.qwe.IConfig;
import io.zero88.qwe.http.server.HttpConfig;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter(value = AccessLevel.PACKAGE)
@NoArgsConstructor
public final class FileStorageConfig implements IConfig {

    public static final String NAME = "__files__";

    private boolean enabled = false;
    private String dir = "files";
    @JsonProperty(value = FileUploadConfig.NAME)
    private FileUploadConfig uploadConfig = new FileUploadConfig();
    @JsonProperty(value = FileDownloadConfig.NAME)
    private FileDownloadConfig downloadConfig = new FileDownloadConfig();

    @Override
    public String key() { return NAME; }

    @Override
    public Class<? extends IConfig> parent() { return HttpConfig.class; }

}
