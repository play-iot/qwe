package io.zero88.qwe.http.server;

import io.zero88.qwe.dto.JsonData;
import io.zero88.qwe.http.HostInfo;
import io.vertx.core.shareddata.Shareable;
import io.vertx.ext.web.Router;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import lombok.Getter;

@Getter
@JsonDeserialize(builder = ServerInfo.Builder.class)
public final class ServerInfo extends HostInfo implements JsonData, Shareable {

    private final String publicHost;
    private final String apiPath;
    private final String wsPath;
    private final String gatewayPath;
    private final String downloadPath;
    private final String uploadPath;
    private final String servicePath;
    private final String webPath;
    private final Router router;

    @lombok.Builder(builderMethodName = "siBuilder")
    ServerInfo(String host, int port, boolean ssl, String publicHost, String apiPath, String wsPath, String gatewayPath,
               String downloadPath, String uploadPath, String servicePath, String webPath, Router router) {
        super(host, port, ssl);
        this.publicHost = publicHost;
        this.apiPath = apiPath;
        this.wsPath = wsPath;
        this.downloadPath = downloadPath;
        this.uploadPath = uploadPath;
        this.servicePath = servicePath;
        this.webPath = webPath;
        this.gatewayPath = gatewayPath;
        this.router = router;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {}

}
