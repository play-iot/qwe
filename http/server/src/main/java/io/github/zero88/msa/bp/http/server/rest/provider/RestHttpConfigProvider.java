package io.github.zero88.msa.bp.http.server.rest.provider;

import io.github.zero88.msa.bp.http.server.HttpConfig;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RestHttpConfigProvider {

    @Getter
    private final HttpConfig httpConfig;

}
