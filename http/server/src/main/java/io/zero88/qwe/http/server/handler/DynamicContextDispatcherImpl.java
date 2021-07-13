package io.zero88.qwe.http.server.handler;

import io.zero88.qwe.http.server.rest.api.DynamicRestApi;
import io.zero88.qwe.micro.ServiceDiscoveryApi;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@RequiredArgsConstructor
final class DynamicContextDispatcherImpl implements DynamicContextDispatcher {

    @Getter
    private final DynamicRestApi api;
    @Getter
    private final String gatewayPath;
    @Getter
    private final ServiceDiscoveryApi dispatcher;

}
