package io.github.zero88.msa.bp.http.server.rest.provider;

import io.github.zero88.msa.bp.BlueprintConfig;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RestBlueprintConfigProvider {

    @Getter
    private final BlueprintConfig bpConfig;

}
