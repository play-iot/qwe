package cloud.playio.qwe.http.server;

import org.jetbrains.annotations.Nullable;

import cloud.playio.qwe.IConfig;

import lombok.NonNull;

/**
 * Represents for router configuration
 */
public interface RouterConfig extends IConfig, HttpSystem {

    /**
     * Defines whether router is enabled or not
     *
     * @return true if enabled, otherwise is false
     */
    boolean isEnabled();

    /**
     * Get configuration path
     *
     * @return configuration path
     */
    @NonNull String getPath();

    /**
     * Get actual path
     *
     * @return a configuration path if isEnabled is true, otherwise is {@code null}
     * @see #isEnabled()
     * @see #getPath()
     */
    default @Nullable String path() {
        return isEnabled() ? getPath() : null;
    }

}
