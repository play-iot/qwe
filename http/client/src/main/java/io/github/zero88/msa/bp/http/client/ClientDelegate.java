package io.github.zero88.msa.bp.http.client;

import java.util.Objects;

import io.github.zero88.msa.bp.IConfig;
import io.github.zero88.msa.bp.dto.JsonData;
import io.github.zero88.msa.bp.http.HostInfo;
import io.github.zero88.msa.bp.http.client.HttpClientConfig.HandlerConfig;
import io.reactivex.Completable;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.impl.HttpClientImpl;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
abstract class ClientDelegate implements IClientDelegate {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Getter
    private final Vertx vertx;
    @NonNull
    private final HttpClient client;
    @Getter
    private final String userAgent;
    @Getter
    private final HandlerConfig handlerConfig;
    @Getter
    private final HostInfo hostInfo;

    ClientDelegate(@NonNull HttpClient client) {
        HttpClientConfig config = new HttpClientConfig(((HttpClientImpl) client).getOptions());
        this.vertx = ((HttpClientImpl) client).getVertx();
        this.userAgent = config.getUserAgent();
        this.handlerConfig = config.getHandlerConfig();
        this.hostInfo = HostInfo.builder()
                                .host(config.getOptions().getDefaultHost())
                                .port(config.getOptions().getDefaultPort())
                                .ssl(config.getOptions().isSsl())
                                .build();
        this.client = client;
    }

    ClientDelegate(@NonNull Vertx vertx, @NonNull HttpClientConfig config) {
        this.userAgent = config.getUserAgent();
        this.handlerConfig = config.getHandlerConfig();
        this.hostInfo = config.getHostInfo();
        this.client = vertx.createHttpClient(config.getOptions());
        this.vertx = vertx;
    }

    static HttpClientConfig cloneConfig(@NonNull HttpClientConfig config, HostInfo hostInfo, int idleTimeout) {
        HostInfo info = evaluateHostInfo(config, hostInfo);
        HttpClientOptions clientOpts = new HttpClientOptions(config.getOptions()).setIdleTimeout(idleTimeout)
                                                                                 .setSsl(info.isSsl())
                                                                                 .setDefaultHost(info.getHost())
                                                                                 .setDefaultPort(info.getPort());
        return IConfig.merge(config,
                             new JsonObject().put("hostInfo", info.toJson()).put("options", clientOpts.toJson()),
                             HttpClientConfig.class);
    }

    private static HostInfo evaluateHostInfo(@NonNull HttpClientConfig clientConfig, HostInfo options) {
        if (Objects.isNull(options)) {
            return clientConfig.getHostInfo();
        }
        return JsonData.from(clientConfig.getHostInfo().toJson().mergeIn(options.toJson(), true), HostInfo.class);
    }

    @Override
    public final HttpClient get() {
        return client;
    }

    @Override
    public Completable close() {
        return getRx().rxClose();
    }

    @Override
    public io.vertx.reactivex.core.http.HttpClient getRx() {
        return io.vertx.reactivex.core.http.HttpClient.newInstance(get());
    }

}
