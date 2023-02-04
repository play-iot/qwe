package cloud.playio.qwe.eventbus.mock;

import java.io.File;
import java.io.IOException;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import cloud.playio.qwe.SharedDataLocalProxy;
import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.dto.msg.RequestFilter;
import cloud.playio.qwe.eventbus.EBContext;
import cloud.playio.qwe.eventbus.EBContract;
import cloud.playio.qwe.eventbus.EBParam;
import cloud.playio.qwe.eventbus.EventAction;
import cloud.playio.qwe.eventbus.EventBusClient;
import cloud.playio.qwe.eventbus.EventListener;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MockWithContextListener implements EventListener {

    @EBContract(action = "GET_ONE")
    public void receive(@EBContext EventAction action, @EBContext Vertx vertx,
                        @EBContext SharedDataLocalProxy dataLocalProxy, @EBContext EventBusClient eventBusClient,
                        RequestData requestData) {
    }

    @EBContract(action = {"CREATE", "UPDATE"})
    public void createOrUpdate(@EBContext EventAction action, @EBParam("id") int id) {
    }

    @EBContract(action = "PATCH")
    public void mix(@EBParam("filter") RequestFilter filter, @EBContext EventAction action,
                    @EBContext EventBusClient eventBusClient, @EBParam("body") JsonObject body) {
    }

    @EBContract(action = "EB")
    public Future<JsonObject> mix(@EBContext Vertx vertx, @EBParam("body") JsonObject body) {
        return Future.succeededFuture(new JsonObject().put("received", body));
    }

    @EBContract(action = "INVOKE")
    public Future<JsonObject> invoke(@EBContext Vertx vertx, @EBParam("body") JsonObject body) throws IOException {
        final String path = File.createTempFile("qwe-", ".json").toPath().toString();
        System.out.println(path);
        return vertx.fileSystem()
                    .writeFile(path, body.toBuffer())
                    .map(ignore -> new JsonObject().put("path", path).put("body", body));
    }

}
