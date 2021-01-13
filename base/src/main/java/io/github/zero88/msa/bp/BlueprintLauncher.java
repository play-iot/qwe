package io.github.zero88.msa.bp;

import java.net.InetSocketAddress;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import io.github.zero88.msa.bp.BlueprintConfig.AppConfig;
import io.github.zero88.msa.bp.BlueprintConfig.DeployConfig;
import io.github.zero88.msa.bp.utils.Configs;
import io.github.zero88.msa.bp.utils.Networks;
import io.github.zero88.utils.Functions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Launcher;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import lombok.NonNull;

public final class BlueprintLauncher extends Launcher {

    private static final Logger logger;

    static {
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
        logger = LoggerFactory.getLogger(BlueprintLauncher.class);
    }

    private BlueprintConfig config;
    private VertxOptions options;
    //    private IClusterDelegate clusterDelegate;

    public static void main(String[] args) {
        new BlueprintLauncher().dispatch(args);
    }

    @Override
    public void afterConfigParsed(JsonObject config) {
        logger.info("Parsing and merging configuration...");
        if (logger.isDebugEnabled()) {
            logger.debug("CONFIG::INPUT: {}", config.encode());
        }
        this.config = IConfig.merge(Configs.loadJsonConfig("system.json"), config, BlueprintConfig.class);
        //        Vertx dummy = Vertx.vertx();
        //        this.config = new ConfigProcessor(dummy).override(fileConfig.toJson(), false, true).orElse
        //        (fileConfig);
        //        dummy.close();
        JsonObject cfg = this.config.toJson();
        if (logger.isDebugEnabled()) {
            logger.debug("CONFIG::FINAL: {}", cfg.encode());
        }
        super.afterConfigParsed(cfg);
    }

    @Override
    public void beforeStartingVertx(VertxOptions options) {
        logger.info("Before starting Vertx instance...");
        this.options = reloadVertxOptions(options);
        super.beforeStartingVertx(this.options);
    }

    @Override
    public void afterStartingVertx(Vertx vertx) {
        if (vertx.isClustered()) {
            //            String addr = config.getSystemConfig().getClusterConfig().getListenerAddress();
            //            ClusterManager clusterManager = this.options.getClusterManager();
            //            if (Strings.isNotBlank(addr)) {
            //                clusterManager.nodeListener(new ClusterNodeListener(clusterDelegate,
            //                                                                    SharedDataDelegate
            //                                                                    .getEventController(vertx,
            //                                                                                                          this.getClass()
            //                                                                                                              .getName()),
            //                                                                    addr));
            //            }
        }
        super.afterStartingVertx(vertx);
    }

    @Override
    public void beforeDeployingVerticle(DeploymentOptions deployOptions) {
        logger.info("Before deploying Verticle...");
        logger.info("Merging configuration...");
        DeploymentOptions options = mergeDeployConfig(deployOptions).setConfig(mergeAppConfig(deployOptions).toJson());
        logger.info("CONFIG::FINAL DEPLOYMENT OPTIONS: {}", options.toJson().encode());
        super.beforeDeployingVerticle(options);
    }

    @Override
    public void afterStoppingVertx() {
        //        ClusterManager clusterManager = this.options.getClusterManager();
        //        if (Objects.nonNull(clusterManager)) {
        //            clusterManager.leave(promise -> {
        //                if (promise.failed()) {
        //                    logger.error("Failed to leave cluster", promise.cause());
        //                }
        //            });
        //        }
        super.afterStoppingVertx();
    }

    private DeployConfig mergeDeployConfig(DeploymentOptions deploymentOptions) {
        JsonObject input = deploymentOptions.toJson();
        input.remove("config");
        if (logger.isDebugEnabled()) {
            logger.debug("CONFIG::INPUT DEPLOYMENT CFG: {}", input.encode());
            logger.debug("CONFIG::CURRENT DEPLOYMENT CFG: {}", config.getDeployConfig().toJson().encode());
        }
        return IConfig.merge(config.getDeployConfig(), input, BlueprintConfig.DeployConfig.class);
    }

    private AppConfig mergeAppConfig(DeploymentOptions deploymentOptions) {
        JsonObject input = deploymentOptions.getConfig();
        if (logger.isDebugEnabled()) {
            logger.debug("CONFIG::INPUT APP CFG: {}", input.encode());
            logger.debug("CONFIG::CURRENT APP CFG: {}", config.getAppConfig().toJson().encode());
        }
        return IConfig.merge(config.getAppConfig(), input, BlueprintConfig.AppConfig.class);
    }

    private VertxOptions reloadVertxOptions(VertxOptions vertxOptions) {
        configEventBus(vertxOptions);
        //        configCluster(vertxOptions);
        final Entry<Long, TimeUnit> e1 = getSystemProp("vertx.blockedThreadCheckInterval",
                                                       VertxOptions.DEFAULT_BLOCKED_THREAD_CHECK_INTERVAL,
                                                       "vertx.blockedThreadCheckIntervalUnit",
                                                       VertxOptions.DEFAULT_BLOCKED_THREAD_CHECK_INTERVAL_UNIT);
        vertxOptions.setBlockedThreadCheckInterval(e1.getKey()).setBlockedThreadCheckIntervalUnit(e1.getValue());
        final Entry<Long, TimeUnit> e2 = getSystemProp("vertx.maxEventLoopExecuteTime",
                                                       VertxOptions.DEFAULT_MAX_EVENT_LOOP_EXECUTE_TIME,
                                                       "vertx.maxEventLoopExecuteTimeUnit",
                                                       VertxOptions.DEFAULT_MAX_EVENT_LOOP_EXECUTE_TIME_UNIT);
        vertxOptions.setMaxEventLoopExecuteTime(e2.getKey()).setMaxEventLoopExecuteTimeUnit(e2.getValue());
        final Entry<Long, TimeUnit> e3 = getSystemProp("vertx.maxWorkerExecuteTime",
                                                       VertxOptions.DEFAULT_MAX_WORKER_EXECUTE_TIME,
                                                       "vertx.maxWorkerExecuteTimeUnit",
                                                       VertxOptions.DEFAULT_MAX_WORKER_EXECUTE_TIME_UNIT);
        vertxOptions.setMaxWorkerExecuteTime(e3.getKey()).setMaxWorkerExecuteTimeUnit(e3.getValue());
        final Entry<Long, TimeUnit> e4 = getSystemProp("vertx.warningExceptionTime", TimeUnit.SECONDS.toNanos(5),
                                                       "vertx.warningExceptionTimeUnit",
                                                       VertxOptions.DEFAULT_WARNING_EXCEPTION_TIME_UNIT);
        vertxOptions.setWarningExceptionTime(e4.getKey()).setWarningExceptionTimeUnit(e4.getValue());
        vertxOptions.setPreferNativeTransport(Boolean.parseBoolean(System.getProperty("vertx.preferNativeTransport",
                                                                                      String.valueOf(
                                                                                          VertxOptions.DEFAULT_PREFER_NATIVE_TRANSPORT))));
        return vertxOptions;
    }

    private Entry<Long, TimeUnit> getSystemProp(@NonNull String propName, long def, @NonNull String propUnitName,
                                                @NonNull TimeUnit defTimeUnit) {
        final String propVal = System.getProperty(propName, String.valueOf(def));
        final long val = Functions.getIfThrow(() -> Functions.toLong().apply(propVal)).orElse(def);
        final Optional<TimeUnit> unitOpt = Optional.ofNullable(System.getProperty(propUnitName))
                                                   .flatMap(ut -> Functions.getIfThrow(() -> TimeUnit.valueOf(ut)));
        return new SimpleEntry<>(val, unitOpt.orElse(val == def ? defTimeUnit : TimeUnit.SECONDS));
    }

    private void configEventBus(VertxOptions options) {
        logger.info("Setup EventBus...");
        EventBusOptions option = this.config.getSystemConfig().getEventBusConfig().getOptions();
        option.setHost(Networks.computeNATAddress(option.getHost()));
        InetSocketAddress address = Networks.computeClusterEventbusUrl(option.getClusterPublicHost(),
                                                                       Networks.validPort(option.getClusterPublicPort(),
                                                                                          option.getPort()));
        if (Objects.nonNull(address)) {
            option.setClusterPublicHost(address.getHostName());
            option.setClusterPublicPort(address.getPort());
        }
        logger.info("Configure EventBus with options: {}", option.toJson().encode());
        options.setEventBusOptions(option);
    }

    //    private void configCluster(VertxOptions options) {
    //        logger.info("Setup Cluster...");
    //        ClusterConfig clusterCfg = config.getSystemConfig().getClusterConfig();
    //        if (Objects.isNull(clusterCfg) || !clusterCfg.isActive()) {
    //            logger.info("Cluster is not activated");
    //            return;
    //        }
    //        logger.info("Configure Cluster with options: {}", clusterCfg.toJson());
    //        logger.info("Cluster type: {}", clusterCfg.getType());
    //        options.setClustered(true);
    //        options.setHAEnabled(clusterCfg.isHa());
    //        this.clusterDelegate = ClusterRegistry.instance().getClusterDelegate(clusterCfg.getType());
    //        if (Objects.isNull(this.clusterDelegate)) {
    //            throw new EngineException("Cannot load cluster type: " + clusterCfg.getType());
    //        }
    //        options.setClusterManager(this.clusterDelegate.initClusterManager(clusterCfg));
    //    }
}
