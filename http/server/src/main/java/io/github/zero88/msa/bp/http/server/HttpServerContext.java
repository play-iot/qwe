package io.github.zero88.msa.bp.http.server;

import io.github.zero88.msa.bp.component.UnitContext;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class HttpServerContext extends UnitContext {

    private ServerInfo serverInfo;

    HttpServerContext setup(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
        return this;
    }

}
