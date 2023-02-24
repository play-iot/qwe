package cloud.playio.qwe.cluster;

import io.github.zero88.utils.Strings;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import cloud.playio.qwe.QWEBootConfig;

import com.hazelcast.config.Config;
import com.hazelcast.internal.config.DeclarativeConfigUtil;

public final class HazelcastManagerFactory implements ClusterManagerFactory {

    @Override
    public ClusterType type() {
        return ClusterType.HAZELCAST;
    }

    public ClusterManager create(QWEBootConfig bootConfig) {
        System.setProperty(DeclarativeConfigUtil.SYSPROP_MEMBER_CONFIG,
                           Strings.fallback(bootConfig.getClusterConfigFile(), "classpath:default-cluster.xml"));
        System.setProperty("hazelcast.logging.type", "log4j2");
        return new HazelcastClusterManager(Config.load().setLiteMember(bootConfig.isClusterLiteMember()));
    }

}
