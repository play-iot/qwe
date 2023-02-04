package cloud.playio.qwe.http.server.proxy.mock;

import cloud.playio.qwe.ApplicationVerticle;
import cloud.playio.qwe.http.server.HttpServerPluginProvider;
import cloud.playio.qwe.http.server.HttpServerRouter;
import cloud.playio.qwe.micro.DiscoveryPluginProvider;

public class MockGatewayServer extends ApplicationVerticle {

    @Override
    public void onStart() {
        this.addProvider(new HttpServerPluginProvider(new HttpServerRouter())).addProvider(new DiscoveryPluginProvider());
    }

    public String configFile() { return "gateway.json"; }

}
