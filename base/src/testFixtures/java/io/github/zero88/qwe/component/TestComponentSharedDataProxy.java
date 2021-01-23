package io.github.zero88.qwe.component;

import io.vertx.core.Vertx;
import io.vertx.core.shareddata.LocalMap;

import lombok.RequiredArgsConstructor;

@SuppressWarnings("unchecked")
@RequiredArgsConstructor
public class TestComponentSharedDataProxy<T extends Component> implements SharedDataLocalProxy {

    private final Vertx vertx;
    private final Class<T> aClass;

    @Override
    public String getSharedKey() {
        return aClass.getName();
    }

    @Override
    public LocalMap<Object, Object> unwrap() {
        return vertx.sharedData().getLocalMap(getSharedKey());
    }

    @Override
    public <D> D getData(String dataKey) {
        return (D) unwrap().get(dataKey);
    }

    @Override
    public <D> D getData(String dataKey, D fallback) {
        return (D) unwrap().getOrDefault(dataKey, fallback);
    }

    @Override
    public <D> D addData(String dataKey, D data) {
        return (D) unwrap().put(dataKey, data);
    }

    @Override
    public <D> D removeData(String dataKey) {
        return (D) unwrap().remove(dataKey);
    }

}
