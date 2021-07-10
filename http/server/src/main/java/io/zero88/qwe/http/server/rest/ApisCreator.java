package io.zero88.qwe.http.server.rest;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import io.zero88.qwe.http.server.HttpSystem.ApisSystem;
import io.zero88.qwe.http.server.RouterConfig;
import io.zero88.qwe.http.server.RouterCreator;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

public abstract class ApisCreator<X, T extends RouterConfig> implements RouterCreator<T>, ApisSystem {

    @Getter(value = AccessLevel.PROTECTED)
    private final Set<Class<? extends X>> apis = new HashSet<>();

    @Override
    public String routerName() {
        return subFunction();
    }

    @Override
    public boolean validate(T config) {
        return !apis.isEmpty();
    }

    @SafeVarargs
    public final ApisCreator<X, T> register(Class<? extends X>... apis) {
        return register(Arrays.asList(apis));
    }

    public final ApisCreator<X, T> register(@NonNull Collection<Class<? extends X>> restApis) {
        restApis.stream().filter(Objects::nonNull).forEach(apis::add);
        return this;
    }

    protected abstract String subFunction();

}
