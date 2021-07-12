package io.zero88.qwe.http.client;

import java.util.function.Supplier;

import io.vertx.core.Future;
import io.vertx.core.http.HttpClient;
import io.zero88.qwe.HasLogger;
import io.zero88.qwe.http.HostInfo;
import io.zero88.qwe.http.client.HttpClientConfig.HandlerConfig;
import io.zero88.qwe.transport.Transporter;

interface IClientDelegate extends Transporter, Supplier<HttpClient>, HasLogger {

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
    Future<Void> close();

}
