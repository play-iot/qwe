package io.zero88.qwe.http.server.config;

import io.zero88.qwe.IConfig;
import io.zero88.qwe.http.server.BasePaths;
import io.zero88.qwe.http.server.RouterConfig;
import io.zero88.qwe.http.server.upload.UploadFileHandler;
import io.zero88.qwe.http.server.upload.UploadListener;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter(value = AccessLevel.PACKAGE)
public final class FileUploadConfig extends AbstractRouterConfig implements IConfig, RouterConfig {

    public static final String NAME = "__upload__";

    private int maxBodySizeMB = 10;
    private String handlerClass = UploadFileHandler.class.getName();
    private String listenerAddress;
    private String listenerClass = UploadListener.class.getName();

    public FileUploadConfig() {
        super(NAME, FileStorageConfig.class);
    }

    @Override
    protected @NonNull String defaultPath() {
        return BasePaths.ROOT_UPLOAD_PATH;
    }

}
