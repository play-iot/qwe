package cloud.playio.qwe.http.server;

import io.github.zero88.utils.Urls;
import io.vertx.core.shareddata.Shareable;
import io.vertx.ext.web.Router;
import cloud.playio.qwe.dto.JsonData;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public final class ServerInfo implements JsonData, Shareable {

    private final String host;
    private final int port;
    private final boolean ssl;
    private final String publicHost;
    private final String apiPath;
    private final String wsPath;
    private final String gatewayPath;
    private final String downloadPath;
    private final String uploadPath;
    private final String servicePath;
    private final String webPath;
    private final Router router;

    static ServerInfo create(HttpServerConfig config, Router router) {
        String proxyServicePath = !config.getApiConfig().getProxyConfig().isEnabled()
                                  ? null
                                  : Urls.combinePath(config.getApiConfig().getPath(),
                                                     config.getApiConfig().getProxyConfig().getPath());
        return ServerInfo.builder()
                         .host(config.getHost())
                         .port(config.getPort())
                         .publicHost(config.getPublicServerUrl())
                         .apiPath(config.getApiConfig().path())
                         .wsPath(config.getWebSocketConfig().path())
                         .gatewayPath(config.getApiGatewayConfig().path())
                         .servicePath(proxyServicePath)
                         .downloadPath(config.getFileDownloadConfig().path())
                         .uploadPath(config.getFileUploadConfig().path())
                         .webPath(config.getStaticWebConfig().path())
                         .router(router)
                         .build();
    }

}
