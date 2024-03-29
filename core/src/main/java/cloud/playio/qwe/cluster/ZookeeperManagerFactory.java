package cloud.playio.qwe.cluster;

import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.zookeeper.ZookeeperClusterManager;
import cloud.playio.qwe.QWEBootConfig;

public final class ZookeeperManagerFactory implements ClusterManagerFactory {

    @Override
    public ClusterType type() {
        return ClusterType.ZOOKEEPER;
    }

    @Override
    public ClusterManager create(QWEBootConfig config) {
        return new ZookeeperClusterManager(config.getClusterConfigFile());
    }

}
