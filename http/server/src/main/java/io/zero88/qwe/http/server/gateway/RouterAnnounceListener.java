package io.zero88.qwe.http.server.gateway;

import io.vertx.servicediscovery.Record;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.micro.ServiceDiscoveryWrapper;
import io.zero88.qwe.micro.monitor.ServiceGatewayAnnounceMonitor;

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
 *         "localAnnounceMonitorClass": "io.zero88.qwe.http.server.gateway.RouterAnnounceListener"
 *       },
 *       "__serviceDiscovery__": {
 *         "enabled": false
 *       },
 *       "__localServiceDiscovery__": {
 *         "enabled": true
 *       },
 *       "__circuitBreaker__": {
 *         "enabled": true
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
public class RouterAnnounceListener extends ServiceGatewayAnnounceMonitor implements DynamicRouterRegister {

    protected RouterAnnounceListener(@NonNull SharedDataLocalProxy proxy, @NonNull ServiceDiscoveryWrapper discovery) {
        super(proxy, discovery);
    }

    @Override
    protected void process(Record record) {
        registerRouter(record);
    }

}
