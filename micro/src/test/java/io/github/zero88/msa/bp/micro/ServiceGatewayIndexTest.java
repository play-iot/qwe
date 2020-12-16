package io.github.zero88.msa.bp.micro;

import org.junit.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;

import io.github.zero88.msa.bp.TestHelper.EventbusHelper;
import io.github.zero88.msa.bp.dto.msg.Filters;
import io.github.zero88.msa.bp.dto.msg.RequestData;
import io.github.zero88.msa.bp.event.EventAction;
import io.github.zero88.msa.bp.event.EventMessage;
import io.github.zero88.msa.bp.event.Status;
import io.github.zero88.msa.bp.micro.ServiceGatewayIndex.Params;
import io.github.zero88.msa.bp.micro.metadata.ServiceNotFoundException;
import io.github.zero88.msa.bp.micro.transfomer.RecordTransformer.RecordView;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

public class ServiceGatewayIndexTest extends BaseMicroServiceTest {

    @Test
    public void test_not_found(TestContext context) {
        final JsonObject expected = new JsonObject().put("code", ServiceNotFoundException.CODE.code())
                                                    .put("message", "Not found service by given parameters: " +
                                                                    "{\"_by\":\"name\",\"identifier\":\"event.not" +
                                                                    ".found\"}");
        testFailed(context, RequestData.builder()
                                       .body(new JsonObject().put(Params.IDENTIFIER, "event.not.found"))
                                       .filter(new JsonObject().put(Params.BY, "name"))
                                       .build(), EventAction.GET_ONE, expected);
    }

    @Test
    public void test_get_by_name(TestContext context) {
        final JsonObject expected = new JsonObject(
            "{\"endpoints\":[{\"method\":\"POST\",\"path\":\"/path\"},{\"method\":\"PUT\"," +
            "\"path\":\"/path/:param\"},{\"method\":\"DELETE\",\"path\":\"/path/:param\"},{\"method\":\"GET\"," +
            "\"path\":\"/path/:param\"},{\"method\":\"GET\",\"path\":\"/path\"},{\"method\":\"PATCH\"," +
            "\"path\":\"/path/:param\"}],\"name\":\"event.record.1\",\"location\":\"event.address.1\"," +
            "\"status\":\"UP\"}");
        testSuccess(context, RequestData.builder()
                                        .body(new JsonObject().put(Params.IDENTIFIER, "event.record.1"))
                                        .filter(new JsonObject().put(Params.BY, "name"))
                                        .build(), EventAction.GET_ONE, expected);
    }

    @Test
    public void test_get_by_name_in_technical_view(TestContext context) {
        final JsonObject value = new JsonObject(
            "{\"endpoints\":[{\"action\":\"UPDATE\",\"method\":\"PUT\",\"capturePath\":\"/path/:param\"," +
            "\"regexPath\":\"/path/.+\"},{\"action\":\"GET_LIST\",\"method\":\"GET\",\"capturePath\":\"/path\"," +
            "\"regexPath\":\"/path\"},{\"action\":\"GET_ONE\",\"method\":\"GET\",\"capturePath\":\"/path/:param\"," +
            "\"regexPath\":\"/path/.+\"},{\"action\":\"REMOVE\",\"method\":\"DELETE\"," +
            "\"capturePath\":\"/path/:param\",\"regexPath\":\"/path/.+\"},{\"action\":\"CREATE\",\"method\":\"POST\"," +
            "\"capturePath\":\"/path\",\"regexPath\":\"/path\"},{\"action\":\"PATCH\",\"method\":\"PATCH\"," +
            "\"capturePath\":\"/path/:param\",\"regexPath\":\"/path/.+\"}],\"name\":\"event.record.1\"," +
            "\"location\":\"event.address.1\",\"status\":\"UP\"}");
        testSuccess(context, RequestData.builder()
                                        .body(new JsonObject().put(Params.IDENTIFIER, "event.record.1"))
                                        .filter(new JsonObject().put(Params.BY, "name")
                                                                .put(Params.VIEW, RecordView.TECHNICAL))
                                        .build(), EventAction.GET_ONE, value);
    }

    @Test
    public void test_get_by_group(TestContext context) {
        final JsonObject value = new JsonObject(
            "{\"name\":\"http.test\",\"location\":\"http://123.456.0.1:1234/api\",\"type\":\"http-endpoint\"," +
            "\"status\":\"UP\"}");
        testSuccess(context, RequestData.builder()
                                        .body(new JsonObject().put(Params.IDENTIFIER, "http"))
                                        .filter(new JsonObject().put(Params.BY, "group"))
                                        .build(), EventAction.GET_ONE, value);
    }

    @Test
    public void test_get_by_path(TestContext context) {
        final JsonObject expected = new JsonObject(
            "{\"endpoints\":[{\"method\":\"GET\",\"path\":\"/xy\"},{\"method\":\"PATCH\",\"path\":\"/xy/:z\"}," +
            "{\"method\":\"PUT\",\"path\":\"/xy/:z\"},{\"method\":\"POST\",\"path\":\"/xy\"},{\"method\":\"DELETE\"," +
            "\"path\":\"/xy/:z\"},{\"method\":\"GET\",\"path\":\"/xy/:z\"}],\"name\":\"event.record.2\"," +
            "\"location\":\"event.address.2\",\"status\":\"UP\"}");
        testSuccess(context, RequestData.builder()
                                        .body(new JsonObject().put(Params.IDENTIFIER, "/xy"))
                                        .filter(new JsonObject().put(Params.BY, "path").put(Filters.PRETTY, true))
                                        .build(), EventAction.GET_ONE, expected);
    }

    @Test
    public void test_list_by_group(TestContext context) {
        final JsonObject expected = new JsonObject(
            "{\"apis\":[{\"name\":\"event.record.1\",\"status\":\"UP\",\"location\":\"event.address.1\"," +
            "\"endpoints\":[{\"method\":\"GET\",\"path\":\"/path/:param\"},{\"method\":\"PUT\"," +
            "\"path\":\"/path/:param\"},{\"method\":\"DELETE\",\"path\":\"/path/:param\"},{\"method\":\"PATCH\"," +
            "\"path\":\"/path/:param\"},{\"method\":\"GET\",\"path\":\"/path\"},{\"method\":\"POST\"," +
            "\"path\":\"/path\"}]},{\"name\":\"event.record.2\",\"status\":\"UP\",\"location\":\"event.address.2\"," +
            "\"endpoints\":[{\"method\":\"GET\",\"path\":\"/xy/:z\"},{\"method\":\"PUT\",\"path\":\"/xy/:z\"}," +
            "{\"method\":\"DELETE\",\"path\":\"/xy/:z\"},{\"method\":\"PATCH\",\"path\":\"/xy/:z\"}," +
            "{\"method\":\"GET\",\"path\":\"/xy\"},{\"method\":\"POST\",\"path\":\"/xy\"}]}]}");
        testSuccess(context, RequestData.builder()
                                        .filter(new JsonObject().put(Params.BY, "group")
                                                                .put(Params.IDENTIFIER, "event.record"))
                                        .build(), EventAction.GET_LIST, expected);
    }

    private void testSuccess(TestContext context, RequestData reqData, EventAction action, JsonObject expected) {
        test(context, reqData, action, expected, Status.SUCCESS);
    }

    private void testFailed(TestContext context, RequestData reqData, EventAction action, JsonObject expected) {
        test(context, reqData, action, expected, Status.FAILED);
    }

    private void test(TestContext context, RequestData reqData, EventAction action, JsonObject expected,
                      Status status) {
        final Async async = context.async();
        final String dataKey = status == Status.FAILED ? "error" : "data";
        final JsonObject response = new JsonObject().put("status", status)
                                                    .put("action", action.action())
                                                    .put(dataKey, expected);
        eventClient.request(config.getGatewayConfig().getIndexAddress(), EventMessage.initial(action, reqData),
                            EventbusHelper.replyAsserter(context, async, response, JSONCompareMode.LENIENT));
    }

}
