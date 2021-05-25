package io.zero88.qwe.http.server.upload;

import java.util.Collections;

import io.github.zero88.utils.Reflections.ReflectionClass;
import io.github.zero88.utils.Strings;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.event.EBContract;
import io.zero88.qwe.event.EventListener;

import lombok.RequiredArgsConstructor;

/**
 * Upload listener to handle uploaded file (update database, transfer to another host)
 */
@RequiredArgsConstructor
public class UploadListener implements EventListener {

    protected final SharedDataLocalProxy proxy;

    public static UploadListener create(SharedDataLocalProxy proxy, String listenerClass) {
        if (Strings.isBlank(listenerClass) || UploadListener.class.getName().equals(listenerClass)) {
            return new UploadListener(proxy);
        }
        return ReflectionClass.createObject(listenerClass, Collections.singletonMap(SharedDataLocalProxy.class, proxy));
    }

    @EBContract(action = "CREATE")
    public Future<JsonObject> create(JsonObject data) { return Future.succeededFuture(data); }

}
