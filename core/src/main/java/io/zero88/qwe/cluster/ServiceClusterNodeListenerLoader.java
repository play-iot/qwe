package io.zero88.qwe.cluster;

import io.vertx.core.ServiceHelper;

public final class ServiceClusterNodeListenerLoader {

    private final ClusterNodeListener clusterNodeListener;

    public ServiceClusterNodeListenerLoader() {
        clusterNodeListener = ServiceHelper.loadFactoryOrNull(ClusterNodeListener.class);
    }

    public ClusterNodeListener listener() {
        return clusterNodeListener;
    }

}
