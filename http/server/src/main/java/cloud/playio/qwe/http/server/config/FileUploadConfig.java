package cloud.playio.qwe.http.server.config;

import java.util.Collections;
import java.util.Set;

import cloud.playio.qwe.http.server.HttpServerConfig;
import cloud.playio.qwe.http.server.HttpSystem.UploadSystem;
import cloud.playio.qwe.http.server.upload.LoggerUploadListener;
import cloud.playio.qwe.http.server.upload.UploadFileHandler;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter(value = AccessLevel.PACKAGE)
public final class FileUploadConfig extends AbstractRouterConfig implements UploadSystem {

    public static final String NAME = "__upload__";

    private int maxBodySizeMB = 10;
    private String uploadDir = "files";
    private String handlerClass = UploadFileHandler.class.getName();
    private Set<String> listenerClasses = Collections.singleton(LoggerUploadListener.class.getName());

    public FileUploadConfig() {
        super(NAME, HttpServerConfig.class);
    }

    @Override
    protected @NonNull String defaultPath() {
        return "/u";
    }

}
