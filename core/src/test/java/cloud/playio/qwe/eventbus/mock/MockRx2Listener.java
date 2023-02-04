package cloud.playio.qwe.eventbus.mock;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import cloud.playio.qwe.eventbus.EBContract;
import cloud.playio.qwe.eventbus.EBParam;
import cloud.playio.qwe.eventbus.EventListener;

public class MockRx2Listener implements EventListener {

    @EBContract(action = "SINGLE")
    public Single<JsonObject> receive(@EBParam("id") int id) {
        return Single.just(new JsonObject().put("resp", id));
    }

    @EBContract(action = "MAYBE")
    public Maybe<Integer> voidFuture(@EBParam("id") int id) {
        return Maybe.empty();
    }

    @EBContract(action = "COMPLETABLE")
    public Completable completable(@EBParam("id") int id) {
        return Completable.complete();
    }

}
