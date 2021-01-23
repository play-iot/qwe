package io.github.zero88.qwe.component;

import io.github.zero88.qwe.event.EventbusClient;
import io.vertx.core.shareddata.LocalMap;

public interface SharedDataLocalProxy extends HasSharedKey {

    /**
     * Data key for EventBus delivery option
     *
     * @see EventbusClient
     */
    String EVENTBUS_OPTION = "EVENTBUS_OPTION";

    <D> D getData(String dataKey);

    <D> D getData(String dataKey, D fallback);

    <D> D addData(String dataKey, D data);

    <D> D removeData(String dataKey);

    LocalMap<Object, Object> unwrap();

}
