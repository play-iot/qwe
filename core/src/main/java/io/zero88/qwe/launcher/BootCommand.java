package io.zero88.qwe.launcher;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.cli.annotations.DefaultValue;
import io.vertx.core.cli.annotations.Description;
import io.vertx.core.cli.annotations.Name;
import io.vertx.core.cli.annotations.Option;
import io.vertx.core.cli.annotations.Summary;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.impl.VertxBuilder;
import io.vertx.core.impl.launcher.commands.BareCommand;
import io.vertx.core.impl.launcher.commands.ExecUtils;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.zero88.qwe.IConfig;
import io.zero88.qwe.QWEBootConfig;
import io.zero88.qwe.cluster.ClusterManagerFactory;
import io.zero88.qwe.cluster.ClusterNodeListener;
import io.zero88.qwe.cluster.ClusterType;
import io.zero88.qwe.cluster.ServiceClusterFactoryLoader;
import io.zero88.qwe.cluster.ServiceClusterNodeListenerLoader;

import lombok.Getter;

@Summary("Creates a clustered QWE instance")
@Description("This command launches a clustered QWE instance but do not deploy any verticles. It will " +
             "receive a verticle if another node of the cluster dies.")
@Name("boot")
public final class BootCommand extends BareCommand {

    protected QWEBootConfig options;
    private boolean ha;
    @Getter
    private String clusterType;
    private boolean clusterLiteMember;
    private String clusterConfigFile;
    private boolean clusterIsMandatory = true;

    /**
     * Enable the high availability (HA) deployment.
     *
     * @param ha high-availability flag, default is false.
     */
    @Option(longName = "ha", acceptValue = false, flag = true)
    @Description(
        "If specified the verticle will be deployed as a high availability (HA) deployment. This means it can " +
        "fail over to any other nodes in the cluster started with the same HA group.")
    public void setHighAvailability(boolean ha) {
        this.ha = ha;
    }

    /**
     * Sets the HA group name.
     *
     * @param group the name of the group, default to {@code __QWE__}.
     */
    @Option(longName = "haGroup", argName = "haGroup")
    @Description(
        "Used in conjunction with -ha this specifies the HA group this node will join. There can be multiple " +
        "HA groups in a cluster. Nodes will only failover to other nodes in the same group. Defaults to '" +
        QWEBootConfig.DEFAULT_HA_GROUP + "'.")
    @DefaultValue(QWEBootConfig.DEFAULT_HA_GROUP)
    public void setHAGroup(String group) {
        this.haGroup = group;
    }

    @Override
    public boolean getHA() {
        return this.ha;
    }

    /**
     * Enable the cluster mode with a specific cluster
     *
     * @param clusterType cluster type, default is false.
     */
    @Option(longName = "cluster-type", argName = "clusterType", choices = {
        "HAZELCAST", "ZOOKEEPER", "INFINISPAN", "IGNITE"
    })
    @Description("Enable cluster mode with a specific type")
    public void setClusterType(String clusterType) {
        this.clusterType = clusterType;
    }

    /**
     * Enable the cluster mode with a specific cluster
     *
     * @param clusterConfigFile cluster type, default is false.
     */
    @Option(longName = "cluster-conf", argName = "clusterConfigFile")
    @Description("Specifies the cluster configuration file corresponding to cluster type")
    public void setClusterConfigFile(String clusterConfigFile) {
        this.clusterConfigFile = clusterConfigFile;
    }

    /**
     * Enable the cluster mode with a specific cluster
     *
     * @param clusterLiteMember cluster type, default is false.
     */
    @Option(longName = "cluster-lite-member", argName = "clusterLiteMember", acceptValue = false, flag = true)
    @Description("Enable cluster lite member. Only for HAZELCAST")
    public void setClusterLiteMember(boolean clusterLiteMember) {
        this.clusterLiteMember = clusterLiteMember;
    }

    @Override
    public boolean isClustered() {
        return Objects.nonNull(this.clusterType);
    }

    /**
     * Make application can be standalone
     *
     * @return a reference to this, so the API can be used fluently
     */
    protected BootCommand canBeStandalone() {
        this.clusterIsMandatory = false;
        return this;
    }

    /**
     * Starts the vert.x instance.
     *
     * @return the created instance of vert.x
     */
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    protected Vertx startVertx() {
        options = normalizeVertxOptions();
        VertxBuilder builder = new VertxBuilder(options);

        beforeStartingVertx(options);
        builder.init();

        Vertx instance;
        if (isClustered()) {
            log.info("Starting clustering...");

            CountDownLatch latch = new CountDownLatch(1);
            AtomicReference<AsyncResult<Vertx>> result = new AtomicReference<>();
            create(builder, ar -> {
                result.set(ar);
                latch.countDown();
            });
            try {
                if (!latch.await(2, TimeUnit.MINUTES)) {
                    log.error("Timed out in starting clustered Vert.x");
                    return null;
                }
            } catch (InterruptedException e) {
                log.error("Thread interrupted in startup");
                Thread.currentThread().interrupt();
                return null;
            }
            if (result.get().failed()) {
                log.error("Failed to form cluster", result.get().cause());
                return null;
            }
            instance = result.get().result();
        } else {
            instance = create(builder);
        }
        addShutdownHook(instance, log, finalAction);
        afterStartingVertx(instance);
        return instance;
    }

    protected QWEBootConfig normalizeVertxOptions() {
        JsonObject cfg = Optional.ofNullable(getJsonFromFileOrString(vertxOptions, "options"))
                                 .orElseGet(JsonObject::new);
        QWEBootConfig config = IConfig.from(cfg, QWEBootConfig.class);
        config.setEventBusOptions(this.getEventBusOptions(config.getEventBusOptions().toJson()));
        configureFromSystemProperties(config, VERTX_OPTIONS_PROP_PREFIX);
        configureFromSystemProperties(config.getMetricsOptions(), METRICS_OPTIONS_PROP_PREFIX);
        config.setClusterType(Optional.ofNullable(clusterType).orElse(config.getClusterType().type()));
        if (!isClustered() && clusterIsMandatory) {
            log.error("Required cluster mode by passing \"-cluster-type\" in CLI option or defining property " +
                      "in a configuration file");
            ExecUtils.exitBecauseOfVertxInitializationIssue();
        }
        if (isClustered()) {
            if (clusterLiteMember) {
                config.setClusterLiteMember(true);
            }
            if (Objects.nonNull(clusterConfigFile)) {
                config.setClusterConfigFile(clusterConfigFile);
            }
            if (!Objects.equals(clusterHost, EventBusOptions.DEFAULT_CLUSTER_HOST)) {
                config.getEventBusOptions().setHost(clusterHost);
            }
            if (clusterPort > EventBusOptions.DEFAULT_CLUSTER_PORT) {
                config.getEventBusOptions().setPort(clusterPort);
            }
            if (!Objects.equals(clusterPublicHost, EventBusOptions.DEFAULT_CLUSTER_PUBLIC_HOST)) {
                config.getEventBusOptions().setClusterPublicHost(clusterPublicHost);
            }
            if (clusterPublicPort > EventBusOptions.DEFAULT_CLUSTER_PUBLIC_PORT) {
                config.getEventBusOptions().setClusterPublicPort(clusterPublicPort);
            } else {
                config.getEventBusOptions().setClusterPublicPort(config.getEventBusOptions().getPort());
            }

            if (getHA()) {
                config.setHAEnabled(true);
                if (haGroup != null) {
                    config.setHAGroup(haGroup);
                }
                if (quorum != -1) {
                    config.setQuorumSize(quorum);
                }
            }
            if (Objects.isNull(config.getEventBusOptions().getClusterNodeMetadata())) {
                config.getEventBusOptions().setClusterNodeMetadata(VersionCommand.getVersion().toJson());
            }
            config.setClusterManager(createClusterManager(config));
        }
        if (log.isDebugEnabled()) {
            if (log.isTraceEnabled()) {
                JsonObject cliOpt = new JsonObject().put(QWEBootConfig.Fields.clusterType, clusterType)
                                                    .put(QWEBootConfig.Fields.clusterLiteMember, clusterLiteMember)
                                                    .put(QWEBootConfig.Fields.clusterConfigFile, clusterConfigFile)
                                                    .put("clusterHost", clusterHost)
                                                    .put("clusterPort", clusterPort)
                                                    .put("clusterPublicHost", clusterPublicHost)
                                                    .put("clusterPublicPort", clusterPublicPort)
                                                    .put("ha", getHA())
                                                    .put("haGroup", haGroup)
                                                    .put("quorum", quorum);
                log.trace("CFG OPTION: " + cfg);
                log.trace("CLI OPTION: " + cliOpt);
            }
            log.debug("QWE BOOT CONFIG: " + config.toJson());
        }
        return config;
    }

    @Override
    protected void afterStartingVertx(Vertx instance) {
        final ClusterNodeListener listener = new ServiceClusterNodeListenerLoader().listener();
        if (listener != null && instance.isClustered()) {
            options.getClusterManager().nodeListener(listener.setup(vertx, options));
        }
        super.afterStartingVertx(instance);
    }

    private ClusterManager createClusterManager(QWEBootConfig config) {
        if (config.getClusterType() == ClusterType.NONE) {
            return null;
        }
        final ClusterManagerFactory factory = new ServiceClusterFactoryLoader().lookup(config.getClusterType());
        if (factory == null) {
            return null;
        }
        return factory.validate().create(config);
    }

}
