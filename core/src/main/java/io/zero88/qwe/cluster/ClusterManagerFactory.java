package io.zero88.qwe.cluster;

import io.github.zero88.utils.Reflections.ReflectionClass;
import io.vertx.core.spi.cluster.ClusterManager;
import io.zero88.qwe.QWEBootConfig;
import io.zero88.qwe.exceptions.InitializerError;

public interface ClusterManagerFactory {

    ClusterType type();

    ClusterManager create(QWEBootConfig config);

    default ClusterManagerFactory validate() {
        if (type().getClasses().stream().anyMatch(c -> ReflectionClass.findClass(c) == null)) {
            throw new InitializerError("Missing " + type().getJars() + " library in classpath");
        }
        return this;
    }

}
