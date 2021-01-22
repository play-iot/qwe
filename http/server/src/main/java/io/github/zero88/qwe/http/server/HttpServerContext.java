package io.github.zero88.qwe.http.server;

import io.github.zero88.qwe.component.ComponentContext;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class HttpServerContext extends ComponentContext {

    private ServerInfo serverInfo;

    HttpServerContext setup(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
        return this;
    }

}
