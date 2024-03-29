package cloud.playio.qwe.http.server.gateway;

import io.vertx.servicediscovery.Record;
import cloud.playio.qwe.SharedDataLocalProxy;
import cloud.playio.qwe.micro.ServiceDiscoveryApi;
import cloud.playio.qwe.micro.monitor.ServiceGatewayAnnounceMonitor;

import lombok.NonNull;

/**
 * Represents listener for event after any service up/down automatically
 * <p>
 * To use it:
 * <ul>
 * <li>Add {@code qwe-micro} in classpath</li>
 * <li>Modify {@code QWEConfig} in json file then override it in {@code Microservice} config. For example:
 * <pre>
 * {
 *   "__app__": {
 *     "__micro__": {
 *       "__gateway__": {
 *         "enabled": true,
 *         "announceMonitorClass": "cloud.playio.qwe.http.server.gateway.RouterAnnounceListener"
 *       }
 *     },
 *     "__http__": {
 *       "__dynamic__": {
 *         "enabled": true,
 *         "path": "/s"
 *       }
 *     }
 *   }
 * }
 * </pre>
 * </li>
 * </ul>
 */
public class RouterAnnounceListener extends ServiceGatewayAnnounceMonitor implements ProxyServiceRouteRegister {

    protected RouterAnnounceListener(@NonNull SharedDataLocalProxy proxy, @NonNull ServiceDiscoveryApi discovery) {
        super(proxy, discovery);
    }

    @Override
    protected void process(Record monitorObject) {
        registerRouter(monitorObject);
    }

}
