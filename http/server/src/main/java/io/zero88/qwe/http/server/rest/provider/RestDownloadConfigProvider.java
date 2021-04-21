package io.zero88.qwe.http.server.rest.provider;

import io.zero88.qwe.http.server.HttpConfig.FileStorageConfig.DownloadConfig;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RestDownloadConfigProvider {

    @Getter
    private final DownloadConfig downloadConfig;

}
