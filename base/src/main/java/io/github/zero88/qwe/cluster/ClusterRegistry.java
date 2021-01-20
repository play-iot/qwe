package io.github.zero88.qwe.cluster;

import java.util.EnumMap;
import java.util.Objects;

import io.github.zero88.utils.Reflections.ReflectionClass;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClusterRegistry {

    private static ClusterRegistry instance;
    private final EnumMap<ClusterType, IClusterDelegate> registry = new EnumMap<>(ClusterType.class);

    public static synchronized void init() {
        if (Objects.nonNull(instance)) {
            throw new IllegalStateException("Machine is already initialized");
        }
        instance = new ClusterRegistry();
        ReflectionClass.stream(ClusterRegistry.class.getPackage().getName(), IClusterDelegate.class,
                               ClusterDelegate.class).forEach(instance::addDelegate);
    }

    public static ClusterRegistry instance() {
        return instance;
    }

    private void addDelegate(Class<IClusterDelegate> delegate) {
        IClusterDelegate clusterDelegate = ReflectionClass.createObject(delegate);
        if (Objects.nonNull(clusterDelegate)) {
            this.registry.put(clusterDelegate.getTypeName(), clusterDelegate);
        }
    }

    public IClusterDelegate getClusterDelegate(ClusterType clusterType) {
        return registry.get(clusterType);
    }

}
