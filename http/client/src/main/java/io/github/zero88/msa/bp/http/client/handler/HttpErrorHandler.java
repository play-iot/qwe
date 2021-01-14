package io.github.zero88.msa.bp.http.client.handler;

import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Objects;

import io.github.zero88.msa.bp.dto.msg.ResponseData;
import io.github.zero88.msa.bp.exceptions.TimeoutException;
import io.github.zero88.msa.bp.exceptions.converter.BlueprintExceptionConverter;
import io.github.zero88.msa.bp.http.HostInfo;
import io.github.zero88.msa.bp.http.client.HttpClientRegistry;
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
        return Single.error(BlueprintExceptionConverter.friendly(error));
    }

}
