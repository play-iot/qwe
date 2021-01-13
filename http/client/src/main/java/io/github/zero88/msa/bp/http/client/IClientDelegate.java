package io.github.zero88.msa.bp.http.client;

import java.util.function.Supplier;

import io.github.zero88.msa.bp.http.HostInfo;
import io.github.zero88.msa.bp.http.client.HttpClientConfig.HandlerConfig;
import io.github.zero88.msa.bp.transport.Transporter;
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
     */
    void close();

    io.vertx.reactivex.core.http.HttpClient getRx();
}
