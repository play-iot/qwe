package io.github.zero88.msa.bp.http.server.rest.provider;

import io.github.zero88.msa.bp.CarlConfig;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RestCarlConfigProvider {

    @Getter
    private final CarlConfig bpConfig;

}
