package cloud.playio.qwe.http.server.proxy.mock;

import io.vertx.core.AsyncResult;
import io.vertx.core.json.JsonObject;
import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.eventbus.EBBody;
import cloud.playio.qwe.eventbus.EBContract;
import cloud.playio.qwe.eventbus.EventBusClient;
import cloud.playio.qwe.eventbus.EventListener;
import cloud.playio.qwe.micro.DiscoveryContext;
import cloud.playio.qwe.micro.RecordHelper;
import cloud.playio.qwe.micro.ServiceDiscoveryApi;
import cloud.playio.qwe.http.EventMethodDefinition;

public class SimilarApiService extends MockEventOneApiOneLocService {

    static String EVENT_1_ADDR = "test.SimilarApiService.1";
    static String EVENT_2_ADDR = "test.SimilarApiService.2";

    @Override
    public void onStart() {
        super.onStart();
        EventBusClient.create(sharedData())
                      .register(EVENT_1_ADDR, new MockSiteListener())
                      .register(EVENT_2_ADDR, new MockProductListener());
    }

    @Override
    protected void publishService(DiscoveryContext discoveryContext) {
        final ServiceDiscoveryApi discovery = discoveryContext.getDiscovery();
        discovery.register(RecordHelper.create("ems-5", EVENT_1_ADDR,
                                               EventMethodDefinition.createDefault("/client/:cId/site", "/:sId")),
                           RecordHelper.create("ems-5", EVENT_2_ADDR,
                                               EventMethodDefinition.createDefault("/client/:cId/site/:sId/product",
                                                                                   "/:pId")))
                 .onComplete(AsyncResult::succeeded);
    }

    static class MockSiteListener implements EventListener {

        @EBContract(action = "GET_LIST")
        public JsonObject list(@EBBody(value = "cId") String clientId) {
            return new JsonObject().put("from", "GET_LIST From site [" + clientId + "]");
        }

        @EBContract(action = "GET_ONE")
        public JsonObject get(RequestData data) {
            return new JsonObject().put("from", "GET_ONE From site");
        }

    }


    static class MockProductListener implements EventListener {

        @EBContract(action = "GET_LIST")
        public JsonObject list(RequestData data) {
            return new JsonObject().put("from", "GET_LIST From product");
        }

        @EBContract(action = "GET_ONE")
        public JsonObject get(RequestData data) {
            return new JsonObject().put("from", "GET_ONE From product");
        }

    }

}
