package io.github.zero88.msa.bp.http.server.gateway;

import org.slf4j.Logger;

import io.github.zero88.msa.bp.micro.ServiceDiscoveryController;
import io.github.zero88.msa.bp.micro.monitor.ServiceGatewayAnnounceMonitor;
import io.vertx.core.Vertx;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.Status;

import lombok.NonNull;

/**
 * Represents listener for event after any service up/down automatically
 * <p>
 * To use it:
 * <ul>
 * <li>Add {@code nube-core-micro} in classpath</li>
 * <li>Modify {@code NubeConfig} in json file then override it in {@code Microservice} config. For example:
 * <pre>
 * {
 *   "__app__": {
 *     "__micro__": {
 *       "__gateway__": {
 *         "enabled": true,
 *         "localAnnounceMonitorClass": "io.github.zero88.msa.bp.http.server.gateway.RouterAnnounceListener"
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

    public RouterAnnounceListener(Vertx vertx, ServiceDiscoveryController controller, String sharedKey) {
        super(vertx, controller, sharedKey);
    }

    @Override
    protected void handle(Record record) {
        if (record.getStatus() == Status.UNKNOWN) {
            rescanService();
        } else {
            register(record);
        }
    }

    @Override
    public @NonNull Logger logger() {
        return logger;
    }

    // TODO: find better way instead force rescan in every register call
    // TODO: for checking in cluster mode
    private void rescanService() {
        getController().getRecords().subscribe(records -> records.forEach(this::register));
    }

}
