package cloud.playio.qwe.http.server.handler;

import io.vertx.ext.web.RoutingContext;
import cloud.playio.qwe.dto.msg.ResponseData;
import cloud.playio.qwe.http.HttpStatusMappingLoader;
import cloud.playio.qwe.http.HttpUtils;

public final class ResponseDataInterceptor implements ResponseInterceptor<ResponseData> {

    @Override
    public void response(RoutingContext ctx, ResponseData resp) {
        ctx.response()
           .setStatusCode(HttpStatusMappingLoader.getInstance().get().success(ctx.request().method()).code())
           .end(HttpUtils.prettify(ctx.request(), resp.body()));
    }

}
