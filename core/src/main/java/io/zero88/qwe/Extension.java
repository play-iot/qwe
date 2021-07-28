package io.zero88.qwe;

import java.nio.file.Path;

/**
 * @param <C> Type of Extension config
 * @param <E> Type of Extension Entrypoint
 * @see ExtensionConfig
 * @see ExtensionEntrypoint
 */
public interface Extension<C extends ExtensionConfig, E extends ExtensionEntrypoint>
    extends HasConfig<C>, HasConfigKey {

    /**
     * Extension name
     *
     * @return extension name
     */
    default String extName() {
        return this.getClass().getName();
    }

    /**
     * Setup {@code Extension} when {@code Application} start
     *
     * @param config     config
     * @param appName    application name
     * @param appDir     application data dir
     * @param sharedData shared data proxy
     * @return a reference to this for fluent API
     */
    Extension<C, E> setup(C config, String appName, Path appDir, SharedDataLocalProxy sharedData);

    /**
     * Stop {@code Extension} when {@code Application} stop
     */
    void stop();

    /**
     * Get extension entrypoint
     *
     * @return entrypoint
     */
    E entrypoint();

}
