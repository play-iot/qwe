package io.github.zero88.qwe.component;

import java.nio.file.Path;

import io.github.zero88.qwe.IConfig;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TestHook<T extends ComponentContext> implements DeployHook<T> {

    @NonNull
    private final Component<IConfig, T> component;
    private final Path dataDir;

    @Override
    public T onSuccess(@NonNull Class<Component<IConfig, T>> aClass, Path dataDir, String sharedKey, String deployId) {
        return this.component.hook().onSuccess(aClass, this.dataDir, aClass.getName(), deployId);
    }

    @Override
    public void onError(Throwable error) {

    }

}
