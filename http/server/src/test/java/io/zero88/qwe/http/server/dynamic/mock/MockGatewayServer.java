package io.zero88.qwe.http.server.dynamic.mock;

import io.zero88.qwe.ApplicationVerticle;
import io.zero88.qwe.http.server.HttpServerProvider;
import io.zero88.qwe.http.server.HttpServerRouter;
import io.zero88.qwe.micro.MicroVerticleProvider;

public class MockGatewayServer extends ApplicationVerticle {

    @Override
    public void start() {
        super.start();
        this.addProvider(new HttpServerProvider(new HttpServerRouter())).addProvider(new MicroVerticleProvider());
    }

    public String configFile() { return "gateway.json"; }

}
