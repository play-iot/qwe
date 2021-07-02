package io.zero88.qwe;

import lombok.NonNull;

public interface PluginDeployHook<T extends PluginContext> {

    /**
     * It will be called after deployed each {@code plugin} successfully from {@code application}
     * <p>
     * Each implementation can enrich any useful information in {@code plugin context} then it can be used later on
     * {@code application} after all plugins are deployed successfully
     *
     * @param context an associate context of plugin
     * @return pluginContext
     * @see Application#onInstallCompleted(PluginContextLookup)
     */
    @SuppressWarnings("unchecked")
    default T onSuccess(@NonNull PluginContext context) {
        return (T) context;
    }

}
