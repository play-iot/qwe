package io.github.zero88.qwe.http.server.rest.provider;

import io.github.zero88.qwe.micro.MicroContext;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RestMicroContextProvider {

    private final MicroContext microContext;

}
