package cloud.playio.qwe.http.server;

import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONCompareMode;

import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import cloud.playio.qwe.TestHelper;
import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.dto.msg.ResponseData;

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
        final RequestOptions options = new RequestOptions(requestOptions()).setMethod(method).setURI(path);
        client().request(options, requestData, true)
                .onSuccess(resp -> expected._assert(context, async, resp))
                .onFailure(context::fail)
                .eventually(v -> {
                    TestHelper.testComplete(async);
                    return Future.succeededFuture();
                });
    }

    default Future<ResponseData> restRequest(TestContext context, HttpMethod method, String path,
                                             RequestData requestData) {
        Async async = context.async();
        final RequestOptions options = new RequestOptions(requestOptions()).setMethod(method).setURI(path);
        return client().request(options, requestData, false).eventually(v -> {
            TestHelper.testComplete(async);
            return Future.succeededFuture();
        });
    }

}
