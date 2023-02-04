package cloud.playio.qwe.http.server;

import java.util.List;

import io.github.zero88.utils.Urls;
import io.vertx.core.http.HttpMethod;
import cloud.playio.qwe.auth.ReqAuthDefinition;
import cloud.playio.qwe.http.EventMethodMapping;
import cloud.playio.qwe.http.HttpUtils;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class RoutePath {

    @EqualsAndHashCode.Include
    private final String path;
    @EqualsAndHashCode.Include
    private final HttpMethod method;
    private final ReqAuthDefinition authDefinition;
    private final List<String> contentTypes;

    public static RoutePath create(EventMethodMapping mapping) {
        return create("/", mapping);
    }

    public static RoutePath create(EventMethodMapping mapping, List<String> contentTypes) {
        return create("/", mapping, contentTypes);
    }

    public static RoutePath create(String basePath, EventMethodMapping mapping) {
        return create(basePath, mapping, HttpUtils.JSON_CONTENT_TYPES);
    }

    public static RoutePath create(String basePath, EventMethodMapping mapping, List<String> contentTypes) {
        return new RoutePath(Urls.combinePath(basePath, mapping.getCapturePath()), mapping.getMethod(),
                             mapping.getAuth(), contentTypes);
    }

}
