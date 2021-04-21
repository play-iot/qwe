package io.zero88.qwe.http.server.gateway;

import io.zero88.qwe.component.SharedDataLocalProxy;
import io.zero88.qwe.micro.ServiceDiscoveryInvoker;
import io.zero88.qwe.micro.monitor.ServiceGatewayAnnounceMonitor;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.Status;

import lombok.NonNull;

/**
 * Represents listener for event after any service up/down automatically
 * <p>
 * To use it:
 * <ul>
 * <li>Add {@code carl-core-micro} in classpath</li>
 * <li>Modify {@code CarlConfig} in json file then override it in {@code Microservice} config. For example:
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

    protected RouterAnnounceListener(@NonNull SharedDataLocalProxy proxy, @NonNull ServiceDiscoveryInvoker controller) {
        super(proxy, controller);
    }

    @Override
    protected void handle(Record record) {
        if (record.getStatus() == Status.UNKNOWN) {
            rescanService();
        } else {
            register(record);
        }
    }

    // TODO: find better way instead force rescan in every register call
    // TODO: for checking in cluster mode
    private void rescanService() {
        getInvoker().getRecords().subscribe(records -> records.forEach(this::register));
    }

}
