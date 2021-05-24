package io.zero88.qwe.cluster;

import io.vertx.core.Vertx;
import io.vertx.core.spi.cluster.NodeListener;
import io.zero88.qwe.QWEBootConfig;

public interface ClusterNodeListener extends NodeListener {

    ClusterNodeListener setup(Vertx vertx, QWEBootConfig config);

}
