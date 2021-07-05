package io.zero88.qwe.http.server.rest.provider;

import io.zero88.qwe.QWEConfig;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RestQWEConfigProvider {

    @Getter
    private final QWEConfig qweConfig;

}
