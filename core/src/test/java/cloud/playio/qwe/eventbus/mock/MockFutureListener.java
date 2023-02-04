package cloud.playio.qwe.eventbus.mock;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import cloud.playio.qwe.eventbus.EBContract;
import cloud.playio.qwe.eventbus.EBParam;
import cloud.playio.qwe.eventbus.EventListener;
import cloud.playio.qwe.exceptions.TimeoutException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MockFutureListener implements EventListener {

    @EBContract(action = "GET_ONE")
    public Future<JsonObject> receive(@EBParam("id") int id) {
        return Future.succeededFuture(new JsonObject().put("resp", id));
    }

    @EBContract(action = "CREATE")
    public Future<Void> voidFuture(@EBParam("id") int id) {
        return Future.succeededFuture();
    }

    @EBContract(action = "ERR")
    public Future<Void> failedFuture(@EBParam("id") int id) {
        return Future.failedFuture(new TimeoutException("alo"));
    }

}
