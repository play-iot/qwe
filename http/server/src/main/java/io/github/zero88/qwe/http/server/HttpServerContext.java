package io.github.zero88.qwe.http.server;

import java.nio.file.Path;

import io.github.zero88.qwe.component.Component;
import io.github.zero88.qwe.component.ComponentContext;

import lombok.Getter;

@Getter
public final class HttpServerContext extends ComponentContext {

    private final ServerInfo serverInfo;

    protected HttpServerContext(Class<? extends Component> componentClz, Path dataDir, String sharedKey,
                                String deployId, ServerInfo serverInfo) {
        super(componentClz, dataDir, sharedKey, deployId);
        this.serverInfo = serverInfo;
    }

}
