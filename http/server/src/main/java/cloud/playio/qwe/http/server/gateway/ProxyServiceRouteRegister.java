package cloud.playio.qwe.http.server.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.zero88.utils.Urls;
import io.vertx.ext.web.Router;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.Status;
import cloud.playio.qwe.http.server.HttpServerPluginContext;
import cloud.playio.qwe.http.server.HttpSystem.GatewaySystem;
import cloud.playio.qwe.http.server.RoutePath;
import cloud.playio.qwe.http.server.RouterCreator;
import cloud.playio.qwe.http.server.ServerInfo;
import cloud.playio.qwe.http.server.rest.api.ProxyServiceApi;
import cloud.playio.qwe.http.server.rest.handler.ProxyServiceDispatcher;
import cloud.playio.qwe.micro.monitor.ServiceGatewayMonitor;

import lombok.NonNull;

public interface ProxyServiceRouteRegister extends ServiceGatewayMonitor, GatewaySystem {

    default @NonNull Logger logger() {
        return LoggerFactory.getLogger(RouterCreator.class);
    }

    default void registerRouter(Record record) {
        ProxyServiceApi api = ProxyServiceApi.create(record);
        ServerInfo serverInfo = sharedData().getData(HttpServerPluginContext.SERVER_INFO_DATA_KEY);
        Router router = serverInfo.getRouter();
        for (RoutePath pPath : api.paths()) {
            String path = Urls.combinePath(serverInfo.getServicePath(), pPath.getPath());
            if (record.getStatus() == Status.UP) {
                ProxyServiceDispatcher handler = ProxyServiceDispatcher.create(getDiscovery(),
                                                                               serverInfo.getServicePath(),
                                                                               pPath.getAuthDefinition(),
                                                                               api::createFilter);
                logger().info(decor("Enable proxy route | API[{}] | Order[{}] | Path[{}::{}]"), api.name(), api.order(),
                              pPath.getMethod(), path);
                router.route(path).order(api.order()).handler(handler).enable();
            } else {
                logger().info(decor("Disable proxy route | API[{}] | Path[{}]"), api.name(), pPath);
                router.route(path).disable();
            }
        }
    }

}
