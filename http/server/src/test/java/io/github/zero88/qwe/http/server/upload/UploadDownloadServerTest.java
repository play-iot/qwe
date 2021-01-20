package io.github.zero88.qwe.http.server.upload;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.zero88.qwe.TestHelper;
import io.github.zero88.qwe.http.server.HttpServerRouter;
import io.github.zero88.qwe.http.server.HttpServerTestBase;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.Timeout;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class UploadDownloadServerTest extends HttpServerTestBase {

    @Rule
    public Timeout timeout = Timeout.seconds(TestHelper.TEST_TIMEOUT_SEC);

    @Override
    protected String httpConfigFile() { return "uploadDownload.json"; }

    @Test
    @Ignore
    //TODO fix it when implementing HTTP client. For test, replace TEST_TIMEOUT_SEC to `3000`
    public void test(TestContext context) {
        Async async = context.async();
        startServer(context, new HttpServerRouter());
    }

}
