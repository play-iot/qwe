package io.github.zero88.msa.bp.http.server.dynamic.mock;

import io.github.zero88.msa.bp.component.ContainerVerticle;
import io.github.zero88.msa.bp.http.server.HttpServerProvider;
import io.github.zero88.msa.bp.http.server.HttpServerRouter;
import io.github.zero88.msa.bp.micro.MicroserviceProvider;

public class MockGatewayServer extends ContainerVerticle {

    @Override
    public void start() {
        super.start();
        this.addProvider(new HttpServerProvider(new HttpServerRouter())).addProvider(new MicroserviceProvider());
    }

    public String configFile() { return "gateway.json"; }

}
