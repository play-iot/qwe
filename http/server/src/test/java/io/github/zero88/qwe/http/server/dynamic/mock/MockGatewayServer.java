package io.github.zero88.qwe.http.server.dynamic.mock;

import io.github.zero88.qwe.component.ApplicationVerticle;
import io.github.zero88.qwe.http.server.HttpServerProvider;
import io.github.zero88.qwe.http.server.HttpServerRouter;
import io.github.zero88.qwe.micro.MicroserviceProvider;

public class MockGatewayServer extends ApplicationVerticle {

    @Override
    public void start() {
        super.start();
        this.addProvider(new HttpServerProvider(new HttpServerRouter())).addProvider(new MicroserviceProvider());
    }

    public String configFile() { return "gateway.json"; }

}
