package io.zero88.qwe;

import io.vertx.core.Future;
import io.vertx.core.Verticle;

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
public interface Application extends HasConfig<QWEAppConfig>, HasSharedKey, HasSharedData, Verticle {

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

    QWEAppConfig appConfig();

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
     * Raise event after installed all component completely
     *
     * @param lookup Context lookup
     */
    void onInstallCompleted(@NonNull ContextLookup lookup);

}
