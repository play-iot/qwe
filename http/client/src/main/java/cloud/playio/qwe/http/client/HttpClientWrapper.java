package cloud.playio.qwe.http.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.Future;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.http.WebSocket;
import io.vertx.core.http.WebSocketConnectOptions;
import io.vertx.core.streams.ReadStream;
import io.vertx.core.streams.WriteStream;

import cloud.playio.qwe.ExtensionEntrypoint;
import cloud.playio.qwe.HasLogger;
import cloud.playio.qwe.Wrapper;
import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.dto.msg.ResponseData;
import cloud.playio.qwe.eventbus.EventMessage;
import cloud.playio.qwe.http.client.handler.WebSocketClientPlan;

public interface HttpClientWrapper extends ExtensionEntrypoint<HttpClientConfig>, HasLogger, Wrapper<HttpClient> {

    //TODO Fix it in discovery plugin
    static HttpClientWrapper wrap(HttpClient client, String userAgent) {
        return new HttpClientWrapperImpl(client, userAgent);
    }

    @Override
    default Logger logger() {
        return LogManager.getLogger(HttpClientWrapper.class);
    }

    /**
     * Represents for identifier based on HTTP client config
     *
     * @return id
     */
    int id();

    /**
     * Open HTTP request
     *
     * @param options request options
     * @return http client request in future
     */
    Future<HttpClientRequest> openRequest(RequestOptions options);

    /**
     * Execute HTTP request
     *
     * @param options Request options
     * @return response data in future
     */
    default Future<ResponseData> request(RequestOptions options) {
        return request(options, null);
    }

    /**
     * Execute HTTP request
     *
     * @param options     Request options
     * @param requestData Request data
     * @return response data in future
     */
    default Future<ResponseData> request(RequestOptions options, RequestData requestData) {
        return request(options, requestData, false);
    }

    /**
     * Execute HTTP request
     *
     * @param options      Request options
     * @param requestData  Request data
     * @param swallowError Swallow error in {@link ResponseData} instead raise exception if {@code HTTP Response status
     *                     code >= 400}
     * @return response data in future
     */
    Future<ResponseData> request(RequestOptions options, RequestData requestData, boolean swallowError);

    /**
     * Upload file in {@code POST} method
     *
     * @param path       Request path
     * @param uploadFile Absolute path for upload file
     * @return response data in future
     * @see #upload(String, AsyncFile)
     */
    Future<ResponseData> upload(String path, String uploadFile);

    /**
     * Upload file in {@code POST} method
     *
     * @param uploadFile File
     * @return response data in future
     * @see #push(ReadStream, HttpMethod)
     */
    default Future<ResponseData> upload(AsyncFile uploadFile) {
        return this.upload(null, uploadFile);
    }

    /**
     * Upload file in {@code POST} method
     *
     * @param path       Request path
     * @param uploadFile File
     * @return response data in future
     * @see #push(String, ReadStream, HttpMethod)
     */
    default Future<ResponseData> upload(String path, AsyncFile uploadFile) {
        return this.push(path, uploadFile, HttpMethod.POST);
    }

    /**
     * Push data from read stream to server. It's useful when redirect data
     *
     * @param readStream Source stream
     * @param method     Http Method
     * @return response data in future
     * @see #push(String, ReadStream, HttpMethod)
     */
    default Future<ResponseData> push(ReadStream readStream, HttpMethod method) {
        return this.push(null, readStream, method);
    }

    /**
     * Push data from read stream to server. It's useful when redirect data
     *
     * @param path       Request options. Override default server host and port
     * @param readStream Source stream
     * @param method     Http Method
     * @return response data in future
     */
    Future<ResponseData> push(String path, ReadStream readStream, HttpMethod method);

    /**
     * Download data from server and save it to local file
     *
     * @param saveFile Save file
     * @return single async file a reference to {@code saveFile }parameter, so the API can be used fluently
     */
    default Future<AsyncFile> download(AsyncFile saveFile) {
        return this.download(null, saveFile);
    }

    /**
     * Download data from server and save it to local file
     *
     * @param path     Request path
     * @param saveFile Save file
     * @return single async file a reference to {@code saveFile }parameter, so the API can be used fluently
     */
    Future<AsyncFile> download(String path, AsyncFile saveFile);

    /**
     * Pull data from server then redirect it to destination stream
     *
     * @param writeStream destination stream
     * @return single {@code WriteStream} a reference to {@code saveFile }parameter, so the API can be used fluently
     */
    default Future<WriteStream> pull(WriteStream writeStream) {
        return this.pull(null, writeStream);
    }

    /**
     * Pull data from server then redirect it to destination stream
     *
     * @param path        Request path
     * @param writeStream destination stream
     * @return single {@code WriteStream} a reference to {@code saveFile }parameter, so the API can be used fluently
     */
    Future<WriteStream> pull(String path, WriteStream writeStream);

    /**
     * Open websocket connection
     *
     * @return eventMessage future contains websocket status
     */
    Future<WebSocket> openWebSocket(WebSocketConnectOptions options);

    /**
     * Open websocket connection
     *
     * @param options WebSocket connection options
     * @param plan    WebSocket client plan for {@code listener} and {@code publisher}
     * @return eventMessage future contains websocket status
     */
    Future<EventMessage> openWebSocket(WebSocketConnectOptions options, WebSocketClientPlan plan);

}
