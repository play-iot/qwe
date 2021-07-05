package io.zero88.qwe.http.server.rest.provider;

import io.zero88.qwe.http.server.HttpServerConfig;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RestHttpConfigProvider {

    @Getter
    private final HttpServerConfig httpConfig;

}
