package cloud.playio.qwe.cluster;

import io.github.zero88.repl.ReflectionClass;
import io.vertx.core.spi.cluster.ClusterManager;
import cloud.playio.qwe.QWEBootConfig;
import cloud.playio.qwe.exceptions.InitializerError;

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
