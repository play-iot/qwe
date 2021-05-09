package io.zero88.qwe.cluster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.spi.cluster.NodeListener;
import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.event.EventBusClient;
import io.zero88.qwe.event.EventMessage;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ClusterNodeListener implements NodeListener {

    private static final Logger logger = LoggerFactory.getLogger(ClusterNodeListener.class);
    private final IClusterDelegate clusterDelegate;
    private final EventBusClient controller;
    private final String listenerAddress;

    @Override
    public void nodeAdded(String nodeID) {
        logger.info("Add node: {}", nodeID);
        ClusterNode clusterNode = clusterDelegate.lookupNodeById(nodeID);
        controller.publish(listenerAddress, EventMessage.initial(EventAction.CREATE, clusterNode.toRequestData()));
    }

    @Override
    public void nodeLeft(String nodeID) {
        logger.info("Remove node: {}", nodeID);
        ClusterNode clusterNode = ClusterNode.builder().id(nodeID).build();
        controller.publish(listenerAddress, EventMessage.initial(EventAction.REMOVE, clusterNode.toRequestData()));
    }

}
