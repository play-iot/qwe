package io.zero88.qwe.micro;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;

import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.servicediscovery.types.HttpLocation;
import io.zero88.qwe.JsonHelper.Junit5;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.dto.msg.RequestFilter;
import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.event.EventMessage;
import io.zero88.qwe.event.Status;
import io.zero88.qwe.exceptions.ErrorCode;
import io.zero88.qwe.http.EventMethodDefinition;
import io.zero88.qwe.micro.filter.ByPredicateFactory;
import io.zero88.qwe.micro.filter.ServiceFilterParam;
import io.zero88.qwe.micro.transfomer.RecordTransformer.ViewType;

public class ServiceLocatorTest extends BaseMicroVerticleTest {

    @Test
    public void test_get_not_found(VertxTestContext context) {
        JsonObject expected = new JsonObject().put("code", ErrorCode.SERVICE_NOT_FOUND.code())
                                              .put("message", "Not found service by given parameters " +
                                                              "[{\"by\":\"name\",\"identifier\":\"not.found\"}]");
        queryOneButFailed(context, RequestData.builder()
                                              .body(new JsonObject().put(ServiceFilterParam.IDENTIFIER, "not.found"))
                                              .filter(new JsonObject().put(ServiceFilterParam.BY, "name"))
                                              .build(), expected);
    }

    @Test
    public void test_get_by_name(VertxTestContext context) {
        final JsonObject expected = new JsonObject(
            "{\"endpoint\":\"ea1\",\"name\":\"er1\",\"status\":\"UP\",\"paths\":[" +
            "{\"method\":\"POST\",\"path\":\"/path\"},{\"method\":\"PUT\",\"path\":\"/path/:param\"}," +
            "{\"method\":\"PATCH\",\"path\":\"/path/:param\"},{\"method\":\"GET\",\"path\":\"/path\"}," +
            "{\"method\":\"GET\",\"path\":\"/path/:param\"},{\"method\":\"DELETE\",\"path\":\"/path/:param\"}]}");
        final RequestData reqData = RequestData.builder()
                                               .body(new JsonObject().put(ServiceFilterParam.IDENTIFIER, "er1"))
                                               .filter(new JsonObject().put(ServiceFilterParam.BY, "name"))
                                               .build();
        discovery.register(RecordHelper.create("er1", "ea1", EventMethodDefinition.createDefault("/path", "/:param")),
                           RecordHelper.create("er2", "ea2", EventMethodDefinition.createDefault("/p", "/:r")))
                 .onSuccess(r -> queryThenAssert(context, reqData, EventAction.GET_ONE, expected));
    }

    @Test
    public void test_get_by_group(VertxTestContext context) {
        JsonObject value = new JsonObject("{\"endpoint\":\"https://1.1.1.1:1234/api\",\"name\":\"http.test\"," +
                                          "\"type\":\"http-endpoint\",\"status\":\"UP\"}");
        RequestData reqData = RequestData.builder()
                                         .body(new JsonObject().put(ServiceFilterParam.IDENTIFIER, "http"))
                                         .filter(new JsonObject().put(ServiceFilterParam.BY, "group"))
                                         .build();
        discovery.register(RecordHelper.create("http.test", new HttpLocation().setHost("1.1.1.1")
                                                                              .setPort(1234)
                                                                              .setSsl(true)
                                                                              .setRoot("/api")))
                 .onSuccess(r -> queryThenAssert(context, reqData, EventAction.GET_ONE, value));
    }

    @Test
    public void test_get_by_path(VertxTestContext context) {
        JsonObject expected = new JsonObject(
            "{\"endpoint\":\"ea2\",\"paths\":[{\"method\":\"POST\",\"path\":\"/xy\"},{\"method\":\"PUT\"," +
            "\"path\":\"/xy/:z\"},{\"method\":\"PATCH\",\"path\":\"/xy/:z\"},{\"method\":\"GET\",\"path\":\"/xy\"}," +
            "{\"method\":\"GET\",\"path\":\"/xy/:z\"},{\"method\":\"DELETE\",\"path\":\"/xy/:z\"}],\"name\":\"er2\"," +
            "\"status\":\"UP\"}");
        RequestData reqData = RequestData.builder()
                                         .body(new JsonObject().put(ServiceFilterParam.IDENTIFIER, "/xy"))
                                         .filter(new JsonObject().put(ServiceFilterParam.BY, ByPredicateFactory.BY_PATH)
                                                                 .put(RequestFilter.PRETTY, true))
                                         .build();
        discovery.register(RecordHelper.create("er2", "ea2", EventMethodDefinition.createDefault("/xy", "/:z")))
                 .onSuccess(r -> queryThenAssert(context, reqData, EventAction.GET_ONE, expected));
    }

    @Test
    public void test_get_by_name_with_technical_view(VertxTestContext context) {
        JsonObject value = new JsonObject(
            "{\"endpoint\":\"ea1\",\"paths\":[{\"action\":\"CREATE\",\"capturePath\":\"/path\"," +
            "\"regexPath\":\"/path\",\"method\":\"POST\"},{\"action\":\"UPDATE\",\"capturePath\":\"/path/:param\"," +
            "\"regexPath\":\"/path/.+\",\"method\":\"PUT\"},{\"action\":\"PATCH\",\"capturePath\":\"/path/:param\"," +
            "\"regexPath\":\"/path/.+\",\"method\":\"PATCH\"},{\"action\":\"GET_LIST\",\"capturePath\":\"/path\"," +
            "\"regexPath\":\"/path\",\"method\":\"GET\"},{\"action\":\"GET_ONE\",\"capturePath\":\"/path/:param\"," +
            "\"regexPath\":\"/path/.+\",\"method\":\"GET\"},{\"action\":\"REMOVE\",\"capturePath\":\"/path/:param\"," +
            "\"regexPath\":\"/path/.+\",\"method\":\"DELETE\"}],\"name\":\"er1\",\"status\":\"UP\"}");
        RequestData reqData = RequestData.builder()
                                         .body(new JsonObject().put(ServiceFilterParam.IDENTIFIER, "er1"))
                                         .filter(new JsonObject().put(ServiceFilterParam.BY, "name")
                                                                 .put(ServiceFilterParam.VIEW, ViewType.TECHNICAL))
                                         .build();
        discovery.register(RecordHelper.create("er1", "ea1", EventMethodDefinition.createDefault("/path", "/:param")))
                 .onSuccess(r -> queryThenAssert(context, reqData, EventAction.GET_ONE, value));
    }

    @Test
    public void test_list_by_group(VertxTestContext context) {
        final JsonObject expected = new JsonObject(
            "{\"apis\":[{\"name\":\"g.er1\",\"status\":\"UP\",\"endpoint\":\"ea1\"," +
            "\"paths\":[{\"method\":\"POST\",\"path\":\"/path\"},{\"method\":\"PUT\",\"path\":\"/path/:param\"}," +
            "{\"method\":\"PATCH\",\"path\":\"/path/:param\"},{\"method\":\"GET\",\"path\":\"/path\"}," +
            "{\"method\":\"GET\",\"path\":\"/path/:param\"},{\"method\":\"DELETE\",\"path\":\"/path/:param\"}]}," +
            "{\"name\":\"g.er2\",\"status\":\"UP\",\"endpoint\":\"ea2\"," +
            "\"paths\":[{\"method\":\"POST\",\"path\":\"/xy\"},{\"method\":\"PUT\",\"path\":\"/xy/:z\"}," +
            "{\"method\":\"PATCH\",\"path\":\"/xy/:z\"},{\"method\":\"GET\",\"path\":\"/xy\"},{\"method\":\"GET\"," +
            "\"path\":\"/xy/:z\"},{\"method\":\"DELETE\",\"path\":\"/xy/:z\"}]}]}");
        final RequestData reqData = RequestData.builder()
                                               .filter(new JsonObject().put(ServiceFilterParam.BY, "group")
                                                                       .put(ServiceFilterParam.IDENTIFIER, "g"))
                                               .build();
        discovery.register(RecordHelper.create("g.er1", "ea1", EventMethodDefinition.createDefault("/path", "/:param")),
                           RecordHelper.create("g.er2", "ea2", EventMethodDefinition.createDefault("/xy", "/:z")))
                 .onSuccess(cf -> queryThenAssert(context, reqData, EventAction.GET_LIST, expected));
    }

    @Test
    public void test_get_one_but_many_record_should_failed(VertxTestContext context) {
        final RequestData reqData = RequestData.builder()
                                               .body(new JsonObject().put(ServiceFilterParam.IDENTIFIER, "g"))
                                               .filter(new JsonObject().put(ServiceFilterParam.BY, "group"))
                                               .build();
        final JsonObject failed = new JsonObject().put("code", "INVALID_ARGUMENT")
                                                  .put("message", "More than one service by given parameters " +
                                                                  "[{\"by\":\"group\",\"identifier\":\"g\"}]");
        discovery.register(RecordHelper.create("g.er1", "ea1", EventMethodDefinition.createDefault("/a", "/:b")),
                           RecordHelper.create("g.er2", "ea2", EventMethodDefinition.createDefault("/x", "/:y")))
                 .onSuccess(cf -> queryOneButFailed(context, reqData, failed));
    }

    @Test
    public void test_get_by_path_combine_name(VertxTestContext context) {
        final JsonObject expected = new JsonObject(
            "{\"endpoint\":\"ea2\",\"paths\":[{\"method\":\"POST\",\"path\":\"/a\"},{\"method\":\"PUT\"," +
            "\"path\":\"/a/:c\"},{\"method\":\"PATCH\",\"path\":\"/a/:c\"},{\"method\":\"GET\",\"path\":\"/a\"}," +
            "{\"method\":\"GET\",\"path\":\"/a/:c\"},{\"method\":\"DELETE\",\"path\":\"/a/:c\"}],\"name\":\"api2\"," +
            "\"status\":\"UP\"}");
        RequestData reqData = RequestData.builder()
                                         .body(new JsonObject().put(ServiceFilterParam.IDENTIFIER, "/a"))
                                         .filter(new JsonObject().put(ServiceFilterParam.BY, ByPredicateFactory.BY_PATH)
                                                                 .put(ServiceFilterParam.NAME, "api1")
                                                                 .put(RequestFilter.PRETTY, true))
                                         .build();
        discovery.register(RecordHelper.create("api1", "ea1", EventMethodDefinition.createDefault("/a/b", "/:c")),
                           RecordHelper.create("api2", "ea2", EventMethodDefinition.createDefault("/a", "/:c")))
                 .onSuccess(cf -> queryThenAssert(context, reqData, EventAction.GET_ONE, expected));
    }

    protected void queryThenAssert(VertxTestContext context, RequestData reqData, EventAction action,
                                   JsonObject expected) {
        test(context, reqData, action, expected, Status.SUCCESS);
    }

    protected void queryOneButFailed(VertxTestContext context, RequestData reqData, JsonObject expected) {
        test(context, reqData, EventAction.GET_ONE, expected, Status.FAILED);
    }

    protected void test(VertxTestContext context, RequestData reqData, EventAction action, JsonObject expected,
                        Status status) {
        final Checkpoint async = context.checkpoint();
        final String dataKey = status == Status.FAILED ? "error" : "data";
        final JsonObject resp = new JsonObject().put("status", status)
                                                .put("action", EventAction.REPLY.action())
                                                .put("prevAction", action.action())
                                                .put(dataKey, expected);
        ebClient.request(getGatewayConfig().getIndexAddress(), EventMessage.initial(action, reqData))
                .onSuccess(msg -> Junit5.assertJson(context, async, resp, msg.toJson(), JSONCompareMode.NON_EXTENSIBLE))
                .onSuccess(c -> context.completeNow());
    }

}
