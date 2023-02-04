package cloud.playio.qwe.example.boot;

import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.impl.VertxInternal;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.core.spi.cluster.NodeInfo;

import cloud.playio.qwe.HasLogger;
import cloud.playio.qwe.QWEBootConfig;
import cloud.playio.qwe.cluster.ClusterNodeListener;

public final class ClusterNodeManager implements ClusterNodeListener, HasLogger {

    private Vertx vertx;
    private QWEBootConfig config;

    @Override
    public ClusterNodeManager setup(Vertx vertx, QWEBootConfig config) {
        this.vertx  = vertx;
        this.config = config;
        return this;
    }

    @Override
    public void nodeAdded(String nodeID) {
        logger().info("Added node: {}", nodeID);
        final ClusterManager clusterManager = config.getClusterManager();
        logger().info("{}", clusterManager.getNodes());
        Promise<NodeInfo> p = vertx instanceof VertxInternal ? ((VertxInternal) vertx).promise() : Promise.promise();
        clusterManager.getNodeInfo(nodeID, p);
        p.future().onComplete(ar -> {
            if (ar.succeeded()) {
                final NodeInfo node = ar.result();
                logger().info("{}:{}----{}", node.host(), node.port(), node.metadata());
            } else {
                logger().error("Lookup failed", ar.cause());
            }
        });
    }

    @Override
    public void nodeLeft(String nodeID) {
        logger().info("Remove node: {}", nodeID);
    }

}
