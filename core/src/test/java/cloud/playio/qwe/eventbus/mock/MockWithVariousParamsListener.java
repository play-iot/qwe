package cloud.playio.qwe.eventbus.mock;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import cloud.playio.qwe.dto.JsonData;
import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.eventbus.EBBody;
import cloud.playio.qwe.eventbus.EBContract;
import cloud.playio.qwe.eventbus.EBParam;

public class MockWithVariousParamsListener extends MockEventListener {

    @EBContract(action = "NOTIFY")
    public void noReturn(JsonObject data) {}

    @EBContract(action = "GET_LIST")
    public String noParam() {return "hello";}

    @EBContract(action = "GET_ONE")
    public int javaParam(@EBParam("id") String id) {
        return Integer.parseInt(id);
    }

    @EBContract(action = "PRIMITIVE")
    public long primitive(@EBParam("id") long id) {
        return id;
    }

    @EBContract(action = "CREATE")
    public RequestData refParam(RequestData data) {return data;}

    @EBContract(action = "PATCH")
    public RequestData overrideRefParam(@EBParam("data") RequestData data) {return data;}

    @EBContract(action = "UPDATE")
    public JsonObject twoRefParams(@EBParam("mock") MockParam param, @EBParam("data") RequestData data) {
        return new JsonObject().put("param", JsonObject.mapFrom(param)).put("request", data.toJson());
    }

    @EBContract(action = "BODY_PART")
    public JsonObject useBodyPartAndHeader(@EBBody("id") Integer id, @EBParam("headers") JsonObject headers) {
        return new JsonObject().put("id", id).put("headers", headers);
    }

    @EBContract(action = "BODY_FULL")
    public JsonObject useBodyAndHeader(@EBBody JsonObject body, @EBParam("headers") JsonObject headers) {
        return new JsonObject().put("id", body).put("headers", headers);
    }

    @EBContract(action = "LIST")
    public JsonObject paramAsList(@EBParam("list") List<String> data) {
        JsonObject result = new JsonObject();
        data.forEach(item -> result.put(item, item));
        return result;
    }

    @EBContract(action = "SET")
    public JsonArray paramAsSet(@EBBody("set") Set<MockParam> data) {
        JsonArray result = new JsonArray();
        data.forEach(result::add);
        return result;
    }

    @EBContract(action = "MAP")
    public JsonObject paramAsMap(@EBBody("map") Map<String, MockParam> data) {
        JsonObject result = new JsonObject();
        data.forEach((s, mockParam) -> result.put(s, JsonData.tryParse(mockParam).toJson()));
        return result;
    }

    @EBContract(action = "COLLECTION")
    public JsonArray paramAsCollection(@EBParam("collection") Collection<MockParam> data) {
        JsonArray result = new JsonArray();
        data.forEach(mockParam -> result.add(JsonData.tryParse(mockParam).toJson()));
        return result;
    }

    @EBContract(action = "ARRAY")
    public JsonArray paramAsArray(@EBParam("array") MockParam[] data) {
        JsonArray result = new JsonArray();
        Arrays.stream(data).forEach(mockParam -> result.add(JsonData.tryParse(mockParam).toJson()));
        return result;
    }

}
