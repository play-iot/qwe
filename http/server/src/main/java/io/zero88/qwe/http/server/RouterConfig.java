package io.zero88.qwe.http.server;

import org.jetbrains.annotations.Nullable;

public interface RouterConfig {

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
    String getPath();

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
