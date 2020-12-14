package io.github.zero88.msa.bp.utils.mock;

import java.util.List;

import io.github.zero88.msa.bp.BlueprintConfig;
import io.github.zero88.msa.bp.cluster.ClusterDelegate;
import io.github.zero88.msa.bp.cluster.ClusterNode;
import io.github.zero88.msa.bp.cluster.ClusterType;
import io.github.zero88.msa.bp.cluster.IClusterDelegate;
import io.vertx.core.spi.cluster.ClusterManager;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@ClusterDelegate
public class MockAnnotationType implements IClusterDelegate {

    @Override
    public ClusterType getTypeName() {
        return ClusterType.IGNITE;
    }

    @Override
    public ClusterManager initClusterManager(BlueprintConfig.SystemConfig.ClusterConfig clusterConfig) {
        return null;
    }

    @Override
    public ClusterNode lookupNodeById(String id) {
        return null;
    }

    @Override
    public List<ClusterNode> getAllNodes() {
        return null;
    }

}
