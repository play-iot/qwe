package io.zero88.qwe.http.client.handler;

import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Objects;

import io.zero88.qwe.dto.msg.ResponseData;
import io.zero88.qwe.exceptions.TimeoutException;
import io.zero88.qwe.exceptions.converter.CarlExceptionConverter;
import io.zero88.qwe.http.HostInfo;
import io.zero88.qwe.http.client.HttpClientRegistry;
import io.github.zero88.utils.Reflections.ReflectionClass;
import io.netty.resolver.dns.DnsNameResolverException;
import io.netty.resolver.dns.DnsNameResolverTimeoutException;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.vertx.core.VertxException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class HttpErrorHandler implements Function<Throwable, Single<ResponseData>> {

    @NonNull
    private final HostInfo hostInfo;

    @SuppressWarnings("unchecked")
    public static <T extends HttpErrorHandler> T create(@NonNull HostInfo hostInfo, Class<T> endHandlerClass) {
        if (Objects.isNull(endHandlerClass) || HttpErrorHandler.class.equals(endHandlerClass)) {
            return (T) new HttpErrorHandler(hostInfo) {};
        }
        return ReflectionClass.createObject(endHandlerClass, Collections.singletonMap(HostInfo.class, hostInfo));
    }

    @Override
    public Single<ResponseData> apply(Throwable error) throws Exception {
        if (error instanceof VertxException && error.getMessage().equals("Connection was closed") ||
            error instanceof DnsNameResolverTimeoutException) {
            HttpClientRegistry.getInstance().remove(hostInfo, false);
            return Single.error(new TimeoutException("Request timeout", error));
        }
        if (error instanceof UnknownHostException || error instanceof DnsNameResolverException) {
            HttpClientRegistry.getInstance().remove(hostInfo, false);
        }
        return Single.error(CarlExceptionConverter.friendly(error));
    }

}
