package io.zero88.qwe.http.client.handler;

import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Function;

import io.github.zero88.utils.Reflections.ReflectionClass;
import io.netty.resolver.dns.DnsNameResolverException;
import io.netty.resolver.dns.DnsNameResolverTimeoutException;
import io.vertx.core.Future;
import io.vertx.core.VertxException;
import io.zero88.qwe.dto.msg.ResponseData;
import io.zero88.qwe.exceptions.TimeoutException;
import io.zero88.qwe.exceptions.converter.CarlExceptionConverter;
import io.zero88.qwe.http.HostInfo;
import io.zero88.qwe.http.client.HttpClientRegistry;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class HttpErrorHandler implements Function<Throwable, Future<ResponseData>> {

    @NonNull
    private final HostInfo hostInfo;

    @SuppressWarnings("unchecked")
    public static <T extends HttpErrorHandler> T create(@NonNull HostInfo hostInfo, Class<T> errorHandlerClass) {
        if (Objects.isNull(errorHandlerClass) || HttpErrorHandler.class.equals(errorHandlerClass)) {
            return (T) new HttpErrorHandler(hostInfo) {};
        }
        return ReflectionClass.createObject(errorHandlerClass, Collections.singletonMap(HostInfo.class, hostInfo));
    }

    @Override
    public Future<ResponseData> apply(Throwable error) {
        if (error instanceof VertxException && error.getMessage().equals("Connection was closed") ||
            error instanceof DnsNameResolverTimeoutException) {
            HttpClientRegistry.getInstance().remove(hostInfo, false);
            return Future.failedFuture(new TimeoutException("Request timeout", error));
        }
        if (error instanceof UnknownHostException || error instanceof DnsNameResolverException) {
            HttpClientRegistry.getInstance().remove(hostInfo, false);
        }
        return Future.failedFuture(CarlExceptionConverter.friendly(error));
    }

}
