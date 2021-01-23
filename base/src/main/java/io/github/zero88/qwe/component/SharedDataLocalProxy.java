package io.github.zero88.qwe.component;

import io.github.zero88.qwe.event.EventbusClient;
import io.vertx.core.Vertx;
import io.vertx.core.shareddata.LocalMap;

@SuppressWarnings("unchecked")
public interface SharedDataLocalProxy extends HasSharedKey {

    /**
     * Data key for EventBus delivery option
     *
     * @see EventbusClient
     */
    String EVENTBUS_OPTION = "EVENTBUS_OPTION";

    Vertx getVertx();

    default <D> D getData(String dataKey) {
        return getData(dataKey, null);
    }

    default <D> D getData(String dataKey, D fallback) {
        return (D) unwrap().getOrDefault(dataKey, fallback);
    }

    default <D> D addData(String dataKey, D data) {
        return (D) unwrap().put(dataKey, data);
    }

    default <D> D removeData(String dataKey) {
        return (D) unwrap().remove(dataKey);
    }

    default LocalMap<Object, Object> unwrap() {
        return getVertx().sharedData().getLocalMap(getSharedKey());
    }

    static SharedDataLocalProxy create(Vertx vertx, String sharedKey) {
        return new SharedDataLocalProxy() {
            @Override
            public Vertx getVertx() {
                return vertx;
            }

            @Override
            public String getSharedKey() {
                return sharedKey;
            }
        };
    }

}
