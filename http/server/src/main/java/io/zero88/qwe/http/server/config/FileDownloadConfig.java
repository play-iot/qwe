package io.zero88.qwe.http.server.config;

import java.util.Collections;
import java.util.Set;

import io.zero88.qwe.http.server.HttpServerConfig;
import io.zero88.qwe.http.server.HttpSystem.DownloadSystem;
import io.zero88.qwe.http.server.download.DownloadFileHandler;
import io.zero88.qwe.http.server.download.LocalDownloadFileListener;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public final class FileDownloadConfig extends AbstractRouterConfig implements DownloadSystem {

    public static final String NAME = "__download__";

    private String downloadDir = "files";
    private String handlerClass = DownloadFileHandler.class.getName();
    private Set<String> listenerClasses = Collections.singleton(LocalDownloadFileListener.class.getName());

    public FileDownloadConfig() {
        super(NAME, HttpServerConfig.class);
    }

    @Override
    protected @NonNull String defaultPath() {
        return "/f";
    }

}
