package io.zero88.qwe;

import java.util.Optional;

import io.vertx.core.Future;
import io.vertx.core.VertxOptions;
import io.vertx.core.impl.VertxImpl;
import io.zero88.qwe.launcher.VersionCommand;

import lombok.NonNull;

/**
 * Represents an {@code Application} contains a list of micro {@code plugins} function.
 * <p>
 * It's mostly deployed as a standalone application by {@code java -jar Application.jar}
 *
 * @see Plugin
 * @see HasConfig
 * @see ApplicationVerticle
 */
public interface Application extends QWEVerticle<QWEAppConfig>, HasAppName, HasSharedKey {

    String DEFAULT_PLUGIN_THREAD_PREFIX = "qwe-plugin-thread-";

    /**
     * Application name
     *
     * @return an application name
     * @apiNote To better identify, {@code Application Name} convention is {@code kebab-case} or {@code snake_case}.
     *     Should avoid {@code space} in name
     */
    default String appName() {
        return Optional.ofNullable(VersionCommand.getVersionOrNull())
                       .flatMap(v -> Optional.ofNullable(v.getName()))
                       .orElse(getClass().getName());
    }

    @Override
    default Class<QWEAppConfig> configClass() {
        return QWEAppConfig.class;
    }

    @Override
    default String configFile() {
        return "app.json";
    }

    default String sharedKey() {
        return this.getClass().getName();
    }

    /**
     * Add plugin provider to startup
     *
     * @param <T>      Type of plugin
     * @param provider Unit provider
     * @return a reference to this, so the API can be used fluently
     */
    <T extends Plugin> Application addProvider(PluginProvider<T> provider);

    /**
     * Compute default plugin pool size
     *
     * @param nbOfPlugins Number of plugins will be installed under this {@code Application}
     * @return default plugin pool size
     */
    default int defaultPluginThreadPoolSize(int nbOfPlugins) {
        int poolSize = VertxOptions.DEFAULT_WORKER_POOL_SIZE;
        if (getVertx() instanceof VertxImpl) {
            poolSize = ((VertxImpl) getVertx()).getOrCreateContext()
                                               .getDeployment()
                                               .deploymentOptions()
                                               .getWorkerPoolSize();
        }
        return Math.max(poolSize / Math.max(nbOfPlugins, 2), 1);
    }

    /**
     * Install the registered {@code plugins} based on the order of given providers of {@link
     * #addProvider(PluginProvider)}
     * <p>
     * If any plugin verticle starts failed, future will catch and report it to {@code Vertx}
     *
     * @return void future
     * @apiNote You can override {@code DeploymentOptions} per {@code plugin} by declared options with key {@link
     *     PluginConfig#deploymentKey()} under {@link QWEAppConfig}
     */
    Future<Void> installPlugins();

    /**
     * Uninstall a list of register plugins when application is stopped
     *
     * @return void future
     */
    Future<Void> uninstallPlugins();

    /**
     * Raise event after all plugins are installed completely
     *
     * @param lookup Context lookup
     */
    void onInstallCompleted(@NonNull PluginContextLookup lookup);

}
