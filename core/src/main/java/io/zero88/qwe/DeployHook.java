package io.zero88.qwe;

import lombok.NonNull;

public interface DeployHook<T extends ComponentContext> {

    /**
     * It will be called after deployed {@code component} success from {@code application}
     * <p>
     * Each implementation can enrich any useful information in {@code component context} then it can be used later on
     * {@code application} after all components are deployed successfully
     *
     * @param context an associate context of component
     * @return ComponentContext
     * @see Application#onInstallCompleted(ContextLookup)
     */
    @SuppressWarnings("unchecked")
    default T onSuccess(@NonNull ComponentContext context) {
        return (T) context;
    }

    default void onError(Throwable error) {

    }

}
