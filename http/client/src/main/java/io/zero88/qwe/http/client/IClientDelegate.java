package io.zero88.qwe.http.client;

import java.util.function.Supplier;

import io.zero88.qwe.http.HostInfo;
import io.zero88.qwe.http.client.HttpClientConfig.HandlerConfig;
import io.zero88.qwe.transport.Transporter;
import io.reactivex.Completable;
import io.vertx.core.http.HttpClient;

interface IClientDelegate extends Transporter, Supplier<HttpClient> {

    /**
     * @return client HTTP agent
     */
    String getUserAgent();

    /**
     * @return handler config
     */
    HandlerConfig getHandlerConfig();

    /**
     * @return HostInfo
     */
    HostInfo getHostInfo();

    /**
     * Close client
     *
     * @return completable
     */
    Completable close();

    io.vertx.reactivex.core.http.HttpClient getRx();

}
