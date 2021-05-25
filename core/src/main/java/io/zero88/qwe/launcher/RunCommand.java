package io.zero88.qwe.launcher;

import java.io.File;
import java.util.Optional;

import io.github.zero88.utils.Strings;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.cli.CLIException;
import io.vertx.core.cli.annotations.DefaultValue;
import io.vertx.core.cli.annotations.Description;
import io.vertx.core.cli.annotations.Name;
import io.vertx.core.cli.annotations.Option;
import io.vertx.core.cli.annotations.Summary;
import io.vertx.core.impl.VertxInternal;
import io.vertx.core.impl.launcher.VertxLifecycleHooks;
import io.vertx.core.impl.launcher.commands.ExecUtils;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.launcher.ExecutionContext;
import io.zero88.qwe.IConfig;
import io.zero88.qwe.QWEBootConfig;
import io.zero88.qwe.QWEConfig;

import lombok.Getter;

@Name(value = "run", priority = 100)
@Summary("Runs a QWE Application verticle")
public final class RunCommand extends io.vertx.core.impl.launcher.commands.RunCommand {

    private final BootCommand delegate = new BootCommand();

    private QWEConfig qweConfig;
    @Getter
    private String clusterType;
    private boolean clusterLiteMember;
    private String clusterConfigFile;

    /**
     * Sets the HA group name.
     *
     * @param group the name of the group, default to {@code __QWE__}.
     */
    @Option(longName = "haGroup", argName = "group")
    @Description(
        "Used in conjunction with -ha this specifies the HA group this node will join. There can be multiple " +
        "HA groups in a cluster. Nodes will only failover to other nodes in the same group. Defaults to '" +
        QWEBootConfig.DEFAULT_HA_GROUP + "'.")
    @DefaultValue(QWEBootConfig.DEFAULT_HA_GROUP)
    public void setHAGroup(String group) {
        this.haGroup = group;
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
    public void setClusteredType(String clusterType) {
        this.clusterType = clusterType;
        this.setCluster(Strings.isNotBlank(this.clusterType));
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
    @Option(longName = "cluster-lite-member", argName = "clusterLiteMember", acceptValue = false)
    @Description("Enable cluster lite mode. Only support HAZELCAST")
    public void setClusterLiteMember(Boolean clusterLiteMember) {
        this.clusterLiteMember = clusterLiteMember;
    }

    @Override
    public void setUp(ExecutionContext context) throws CLIException {
        super.setUp(context);
        qweConfig = IConfig.from(Optional.ofNullable(getConfiguration()).orElseGet(JsonObject::new), QWEConfig.class);
        delegate.setHighAvailability(ha);
        delegate.setHAGroup(haGroup);
        delegate.setClusterType(clusterType);
        delegate.setClusterLiteMember(clusterLiteMember);
        delegate.setClusterConfigFile(clusterConfigFile);
        delegate.setClusterHost(clusterHost);
        delegate.setClusterPort(clusterPort);
        delegate.setClusterPublicHost(clusterPublicHost);
        delegate.setClusterPublicPort(clusterPublicPort);
        delegate.setQuorum(quorum);
        delegate.setSystemProps(systemProperties);
        delegate.setCwd(getCwd());
        delegate.setClasspath(
            Optional.ofNullable(classpath).map(cp -> String.join(File.pathSeparator, cp)).orElse(null));
        delegate.setVertxOptions(Optional.ofNullable(qweConfig.getBootConfig())
                                         .map(QWEBootConfig::toJson)
                                         .map(JsonObject::encode)
                                         .orElseGet(
                                             () -> Optional.ofNullable(getJsonFromFileOrString(vertxOptions, "options"))
                                                           .map(JsonObject::encode)
                                                           .orElse(null)));
        delegate.canBeStandalone().setUp(context);
    }

    @Override
    public void run() {
        if (redeploy == null || redeploy.isEmpty()) {
            JsonObject conf = qweConfig.toJson();
            afterConfigParsed(conf);

            doRun();

            deploymentOptions = qweConfig.getDeployConfig();
            configureFromSystemProperties(deploymentOptions, DEPLOYMENT_OPTIONS_PROP_PREFIX);
            deploymentOptions.setConfig(qweConfig.getAppConfig().toJson())
                             .setWorker(worker)
                             .setHa(ha)
                             .setInstances(instances);
            beforeDeployingVerticle(deploymentOptions);
            deploy();
        } else {
            // redeploy is set, start the redeployment infrastructure (watcher).
            initializeRedeployment();
        }
    }

    protected void doRun() {
        delegate.run(this::afterStoppingVertx);
        vertx = delegate.vertx();

        if (vertx == null) {
            // Already logged.
            ExecUtils.exitBecauseOfVertxInitializationIssue();
        }

        if (vertx instanceof VertxInternal) {
            ((VertxInternal) vertx).addCloseHook(completionHandler -> {
                try {
                    beforeStoppingVertx(vertx);
                    completionHandler.handle(Future.succeededFuture());
                } catch (Exception e) {
                    completionHandler.handle(Future.failedFuture(e));
                }
            });
        }
    }

    protected void deploy() {
        deploy(mainVerticle, vertx(), deploymentOptions, res -> {
            if (res.failed()) {
                handleDeployFailed(res.cause());
            }
        });
    }

    @Override
    public synchronized Vertx vertx() {
        return delegate.vertx();
    }

    private void handleDeployFailed(Throwable cause) {
        if (executionContext.main() instanceof VertxLifecycleHooks) {
            ((VertxLifecycleHooks) executionContext.main()).handleDeployFailed(vertx(), mainVerticle, deploymentOptions,
                                                                               cause);
        } else {
            ExecUtils.exitBecauseOfVertxDeploymentIssue();
        }
    }

}
