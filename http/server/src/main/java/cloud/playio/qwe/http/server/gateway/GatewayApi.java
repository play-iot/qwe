package cloud.playio.qwe.http.server.gateway;

import cloud.playio.qwe.SharedDataLocalProxy;
import cloud.playio.qwe.http.server.config.ApiGatewayConfig;
import cloud.playio.qwe.http.server.rest.api.IRestEventApi;

public interface GatewayApi extends IRestEventApi<ApiGatewayConfig> {

    @Override
    GatewayApi setup(ApiGatewayConfig config, SharedDataLocalProxy sharedData);

}
