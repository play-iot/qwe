package io.github.zero88.qwe.component;

import io.vertx.core.Vertx;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ComponentSharedDataHelper {

    public static <T extends Component> SharedDataLocalProxy create(Vertx vertx, Class<T> aClass) {
        return SharedDataLocalProxy.create(vertx, aClass.getName());
    }

}
