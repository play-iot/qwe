package io.zero88.qwe.http.server.rest.provider;

import io.zero88.qwe.CarlConfig;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RestCarlConfigProvider {

    @Getter
    private final CarlConfig bpConfig;

}
