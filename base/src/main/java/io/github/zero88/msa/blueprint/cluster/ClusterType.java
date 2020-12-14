package io.github.zero88.msa.blueprint.cluster;

import java.io.Serializable;

import io.vertx.core.shareddata.Shareable;

public enum ClusterType implements Shareable, Serializable {

    HAZELCAST, ZOOKEEPER, INFINISPAN, IGNITE, KAFKA

}
