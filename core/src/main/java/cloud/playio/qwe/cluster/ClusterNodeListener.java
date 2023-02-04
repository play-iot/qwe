package cloud.playio.qwe.cluster;

import io.vertx.core.Vertx;
import io.vertx.core.spi.cluster.NodeListener;
import cloud.playio.qwe.QWEBootConfig;

public interface ClusterNodeListener extends NodeListener {

    ClusterNodeListener setup(Vertx vertx, QWEBootConfig config);

}
