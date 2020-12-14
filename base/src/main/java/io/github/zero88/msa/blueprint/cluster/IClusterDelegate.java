package io.github.zero88.msa.blueprint.cluster;

import java.util.List;

import io.github.zero88.msa.blueprint.BlueprintConfig;
import io.github.zero88.msa.blueprint.exceptions.NotFoundException;
import io.vertx.core.spi.cluster.ClusterManager;

import lombok.NonNull;

public interface IClusterDelegate {

    @NonNull ClusterType getTypeName();

    @NonNull ClusterManager initClusterManager(BlueprintConfig.SystemConfig.ClusterConfig clusterConfig);

    /**
     * Find node in cluster.
     *
     * @param id node Id
     * @return cluster node
     * @throws ClusterException  if cluster manager was not initialized
     * @throws NotFoundException if cluster does not have node with given id
     */
    ClusterNode lookupNodeById(String id);

    List<ClusterNode> getAllNodes();

}
