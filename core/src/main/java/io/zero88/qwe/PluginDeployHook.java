package io.zero88.qwe;

import lombok.NonNull;

/**
 * A plugin deployment hook that helps injecting a {@code plugin context} when deploying {@code Plugin} under {@code
 * Application}
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public interface PluginDeployHook {

    /**
     * Inject a pre context before deploying {@code Plugin}
     * <p>
     * It will be called automatically in a pre-deploy {@code Plugin} step of {@code Application} deployment workflow
     *
     * @param plugin     a current plugin
     * @param preContext an associate pre-context of plugin
     * @return a pre plugin context
     */
    default Plugin onPreDeploy(@NonNull Plugin plugin, @NonNull PluginContext preContext) {
        return plugin.setup(enrichContext(preContext, false));
    }

    /**
     * Inject a post context after deployed {@code Plugin}
     * <p>
     * It will be called automatically in a post-deploy {@code plugin} step of {@code Application} deployment workflow
     * <p>
     * Depends on a specified plugin business, you can override the post plugin context by using {@link
     * #enrichContext(PluginContext, boolean)}
     *
     * @param plugin      a current plugin
     * @param postContext an associate post-context of plugin
     * @return a post plugin context
     */
    default Plugin onPostDeploy(@NonNull Plugin plugin, @NonNull PluginContext postContext) {
        return plugin.setup(enrichContext(postContext, true));
    }

    /**
     * Each implementation can enrich any useful information in {@code plugin context} then it can be used later on
     * {@code application} after all plugins are deployed successfully
     *
     * @param pluginContext an associate post-context of plugin
     * @param isPostStep    a flag to identifies whether is pre-step or post-step
     * @return a plugin context
     * @see Application#onInstallCompleted(ApplicationContextHolder)
     */
    default PluginContext enrichContext(@NonNull PluginContext pluginContext, boolean isPostStep) {
        return pluginContext;
    }

}
