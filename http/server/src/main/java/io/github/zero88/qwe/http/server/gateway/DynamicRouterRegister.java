package io.github.zero88.qwe.http.server.gateway;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;

import io.github.zero88.qwe.exceptions.CarlException;
import io.github.zero88.qwe.http.server.HttpServer;
import io.github.zero88.qwe.http.server.ServerInfo;
import io.github.zero88.qwe.http.server.handler.DynamicContextDispatcher;
import io.github.zero88.qwe.http.server.rest.DynamicRestApi;
import io.github.zero88.qwe.micro.monitor.ServiceGatewayMonitor;
import io.github.zero88.utils.Urls;
import io.vertx.ext.web.Router;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.Status;

import lombok.NonNull;

public interface DynamicRouterRegister extends ServiceGatewayMonitor {

    @NonNull Logger logger();

    default boolean register(Record record) {
        try {
            DynamicRestApi api = DynamicRestApi.create(record);
            ServerInfo serverInfo = sharedData().getData(HttpServer.SERVER_INFO_DATA_KEY);
            Router router = serverInfo.getRouter();
            String gatewayPath = Urls.combinePath(serverInfo.getApiPath(), serverInfo.getServicePath());
            List<String> paths = api.alternativePaths()
                                    .orElse(Collections.singleton(api.path()))
                                    .stream()
                                    .map(p -> Urls.combinePath(gatewayPath, p))
                                    .sorted(Comparator.reverseOrder())
                                    .collect(Collectors.toList());
            if (record.getStatus() == Status.UP) {
                DynamicContextDispatcher<DynamicRestApi> handler = DynamicContextDispatcher.create(api, gatewayPath,
                                                                                                   getController());
                paths.forEach(path -> {
                    logger().info("Enable dynamic route | API: {} | Order: {} | Path: {}", api.name(), api.order(),
                                  path);
                    router.route(path).order(api.order()).handler(handler).enable();
                });
            } else {
                paths.forEach(path -> {
                    logger().info("Disable dynamic route | API: {} | Path: {}", api.name(), path);
                    router.route(path).disable();
                });
            }
            return true;
        } catch (CarlException e) {
            logger().warn("Cannot register Dynamic service", e);
            return false;
        }
    }

}
