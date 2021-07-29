package io.zero88.qwe.http.server;

import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONCompareMode;

import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.dto.msg.ResponseData;

public interface RestApiTestHelper extends HttpServerTestHelper {

    default void sendToApiThenAssert(TestContext context, HttpMethod method, String path, int codeExpected,
                                     JsonObject bodyExpected, Customization... customizations) {
        sendToApiThenAssert(context, method, path, codeExpected, bodyExpected, JSONCompareMode.STRICT, customizations);
    }

    default void sendToApiThenAssert(TestContext context, HttpMethod method, String path, int codeExpected,
                                     JsonObject bodyExpected, JSONCompareMode mode, Customization... customizations) {
        sendToApiThenAssert(context, method, path, null, ExpectedResponse.builder()
                                                                         .expected(bodyExpected)
                                                                         .code(codeExpected)
                                                                         .customizations(customizations)
                                                                         .mode(mode)
                                                                         .build());
    }

    default void sendToApiThenAssert(TestContext context, HttpMethod method, String path, RequestData requestData,
                                     int codeExpected, JsonObject bodyExpected, Customization... customizations) {
        sendToApiThenAssert(context, method, path, requestData, ExpectedResponse.builder()
                                                                                .expected(bodyExpected)
                                                                                .code(codeExpected)
                                                                                .customizations(customizations)
                                                                                .mode(JSONCompareMode.STRICT)
                                                                                .build());
    }

    default void sendToApiThenAssert(TestContext context, HttpMethod method, String path, RequestData requestData,
                                     ExpectedResponse expected) {
        Async async = context.async(expected.hasAfter() ? 2 : 1);
        //FIXME
        //        HttpClientDelegate.create(vertx(), HostInfo.from(requestOptions()))
        //                          .request(path, method, requestData)
        //                          .onSuccess(resp -> expected._assert(context, async, resp))
        //                          .onFailure(context::fail)
        //                          .eventually(v -> {
        //                              TestHelper.testComplete(async);
        //                              return Future.succeededFuture();
        //                          });
    }

    default Future<ResponseData> restRequest(TestContext context, HttpMethod method, String path,
                                             RequestData requestData) {
        Async async = context.async();
        //FIXME
        return Future.succeededFuture();
        //        return HttpClientDelegate.create(vertx(), HostInfo.from(requestOptions()))
        //                                 .request(path, method, requestData)
        //                                 .eventually(v -> {
        //                                     TestHelper.testComplete(async);
        //                                     return Future.succeededFuture();
        //                                 });
    }

}
