package cloud.playio.qwe.http.server.upload;

import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.multipart.MultipartForm;
import cloud.playio.qwe.JsonHelper;
import cloud.playio.qwe.http.server.HttpServerPluginTestBase;
import cloud.playio.qwe.http.server.HttpServerRouter;

@RunWith(VertxUnitRunner.class)
public class UploadDownloadServerTest extends HttpServerPluginTestBase {

    @Override
    protected String httpConfigFile() {return "uploadDownload.json";}

    @Test
    public void test_default_logger_upload_listener(TestContext context) {
        JsonObject expected = new JsonObject(
            "{\"attributes\":{\"description\":\"hello\"},\"files\":[{\"charset\":\"UTF-8\",\"fileName\":\"test.txt\"," +
            "\"extension\":\"txt\",\"transferEncoding\":\"7bit\",\"size\":14680064,\"name\":\"abc\"," +
            "\"contentType\":\"text/plain\"}]}");
        Async async = context.async();
        startServer(context, new HttpServerRouter());
        RequestOptions options = requestOptions().setURI("/u");
        MultipartForm form = MultipartForm.create()
                                          .attribute("description", "hello")
                                          .textFileUpload("abc", "test.txt", Buffer.buffer("content"), "text/plain");

        WebClient.create(vertx)
                 .post(options.getPort(), options.getHost(), options.getURI())
                 .sendMultipartForm(form)
                 .onSuccess(resp -> JsonHelper.Junit4.assertJson(context, async, expected, resp.bodyAsJsonObject()))
                 .onFailure(context::fail);
    }

}
