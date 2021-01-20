package io.github.zero88.qwe.http.server.rest.provider;

import io.github.zero88.qwe.http.client.HttpClientConfig;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RestHttpClientConfigProvider {

    @Getter
    private final HttpClientConfig httpClientConfig;

}
