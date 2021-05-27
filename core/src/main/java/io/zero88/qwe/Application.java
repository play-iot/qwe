package io.zero88.qwe;

import java.util.Optional;

import io.vertx.core.Future;
import io.zero88.qwe.launcher.VersionCommand;

import lombok.NonNull;

/**
 * Represents an {@code Application} contains a list of micro {@code Components} function.
 * <p>
 * It's mostly deployed as a standalone application by {@code java -jar Application.jar}
 *
 * @see Component
 * @see HasConfig
 * @see ApplicationVerticle
 */
public interface Application extends HasSharedKey, QWEVerticle<QWEAppConfig> {

    /**
     * Application name
     *
     * @return an application name
     */
    default String appName() {
        return Optional.ofNullable(VersionCommand.getVersionOrNull())
                       .flatMap(v -> Optional.ofNullable(v.getName()))
                       .orElse(getClass().getName());
    }

    QWEAppConfig appConfig();

    @Override
    default Class<QWEAppConfig> configClass() {
        return QWEAppConfig.class;
    }

    @Override
    default String configFile() {
        return "app.json";
    }

    default String getSharedKey() {
        return this.getClass().getName();
    }

    /**
     * Add component provider to startup
     *
     * @param <T>      Type of component
     * @param provider Unit provider
     * @return a reference to this, so the API can be used fluently
     */
    <T extends Component> Application addProvider(ComponentProvider<T> provider);

    /**
     * Install a list of register component verticle based on the order of given providers of {@link
     * #addProvider(ComponentProvider)}
     * <p>
     * If any component verticle starts failed, future will catch and report it to {@code Vertx}
     *
     * @return void future
     */
    Future<Void> installComponents();

    /**
     * Uninstall a list of register components when application is stopped
     *
     * @return void future
     */
    Future<Void> uninstallComponents();

    /**
     * Raise event after all component are installed completely
     *
     * @param lookup Context lookup
     */
    void onInstallCompleted(@NonNull ContextLookup lookup);

}
