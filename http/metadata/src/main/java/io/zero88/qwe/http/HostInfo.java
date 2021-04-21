package io.zero88.qwe.http;

import java.util.Optional;

import io.zero88.qwe.dto.JsonData;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.shareddata.Shareable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder(builderClassName = "Builder")
@JsonDeserialize(builder = HostInfo.Builder.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class HostInfo implements JsonData, Shareable {

    @Include
    protected final String host;
    @Include
    protected final int port;
    @Include
    protected final boolean ssl;

    protected HostInfo(String host, int port, boolean ssl) {
        this.host = host;
        this.port = port;
        this.ssl = ssl;
    }

    protected HostInfo(@NonNull HostInfo hostInfo) {
        this.host = hostInfo.host;
        this.port = hostInfo.port;
        this.ssl = hostInfo.ssl;
    }

    public static HostInfo from(RequestOptions options) {
        return HostInfo.builder()
                       .host(options.getHost())
                       .port(options.getPort())
                       .ssl(Optional.ofNullable(options.isSsl()).orElse(false))
                       .build();
    }

    public RequestOptions to(String path) {
        return new RequestOptions().setHost(host).setPort(port).setSsl(ssl).setURI(path);
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {

        public HostInfo build() {
            int port = this.port == 0 ? this.ssl ? 443 : 80 : this.port;
            return new HostInfo(this.host, port, this.ssl);
        }

    }

}
