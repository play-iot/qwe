package io.zero88.qwe;

import java.nio.file.Path;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
     * @param sharedData shared data proxy
     * @param appName    application name
     * @param appDir     application data dir
     * @param config     extension config
     * @return a reference to this for fluent API
     */
    Extension<C, E> setup(@NotNull SharedDataLocalProxy sharedData, @NotNull String appName, @NotNull Path appDir,
                          @Nullable C config);

    /**
     * Stop {@code Extension} when {@code Application} stop
     */
    void stop();

    /**
     * Get extension config
     *
     * @return extension config
     */
    C extConfig();

    /**
     * Get extension entrypoint
     *
     * @return entrypoint
     */
    E entrypoint();

}
