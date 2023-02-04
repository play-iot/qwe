package cloud.playio.qwe.cluster;

import io.vertx.core.ServiceHelper;

public final class ClusterNodeListenerServiceLoader {

    private final ClusterNodeListener clusterNodeListener;

    public ClusterNodeListenerServiceLoader() {
        clusterNodeListener = ServiceHelper.loadFactoryOrNull(ClusterNodeListener.class);
    }

    public ClusterNodeListener listener() {
        return clusterNodeListener;
    }

}
