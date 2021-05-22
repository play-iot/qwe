package io.zero88.qwe;

import io.vertx.core.Vertx;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import io.zero88.qwe.event.EventBusClient;

@SuppressWarnings("unchecked")
public interface SharedDataLocalProxy extends HasSharedKey {

    /**
     * Data key for EventBus delivery option
     *
     * @see EventBusClient
     */
    String EVENTBUS_DELIVERY_OPTION = "EVENTBUS_OPTION";
    String APP_DATADIR = "APP_DATADIR";

    Vertx getVertx();

    default <D> D getData(String dataKey) {
        return getData(dataKey, null);
    }

    default <D> D getData(String dataKey, D fallback) {
        return (D) localData().getOrDefault(dataKey, fallback);
    }

    default <D> D addData(String dataKey, D data) {
        return (D) localData().put(dataKey, data);
    }

    default <D> D removeData(String dataKey) {
        return (D) localData().remove(dataKey);
    }

    default LocalMap<Object, Object> localData() {
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

    default SharedData unwrap() {
        return getVertx().sharedData();
    }

}
