package io.github.zero88.msa.bp.http.server.rest.provider;


import io.github.zero88.msa.bp.http.server.HttpConfig.FileStorageConfig.DownloadConfig;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RestDownloadConfigProvider {

    @Getter
    private final DownloadConfig downloadConfig;

}
