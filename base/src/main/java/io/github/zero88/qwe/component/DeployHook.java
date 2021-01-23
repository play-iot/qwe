package io.github.zero88.qwe.component;

import java.nio.file.Path;

import io.github.zero88.qwe.IConfig;

import lombok.NonNull;

public interface DeployHook<T extends ComponentContext> {

    /**
     * It will be called after deployed componentClass success from application
     *
     * @param aClass    an associate componentClass class
     * @param dataDir   a current application data dir
     * @param sharedKey a key to access shared data from {@code Application}
     * @param deployId  a deployment id
     * @return ComponentContext
     */
    @SuppressWarnings("unchecked")
    default T onSuccess(@NonNull Class<Component<IConfig, T>> aClass, Path dataDir, String sharedKey, String deployId) {
        return (T) new ComponentContext(aClass, dataDir, sharedKey, deployId);
    }

    default void onError(Throwable error) {

    }

}
