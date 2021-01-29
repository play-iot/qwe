package io.github.zero88.qwe.http.server;

import io.github.zero88.utils.Urls;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BasePaths {

    public static final String WEB_PATH = "/web";
    public static final String ROOT_API_PATH = "/api";
    public static final String ROOT_GATEWAY_PATH = "/gw";
    public static final String ROOT_WS_PATH = "/ws";
    public static final String DYNAMIC_API_PATH = "/s";
    public static final String DYNAMIC_WS_PATH = "/s/ws";
    public static final String ROOT_UPLOAD_PATH = "/u";
    public static final String ROOT_DOWNLOAD_PATH = "/f";
    public static final String WILDCARDS_ANY_PATH = "*";

    public static String addWildcards(String path) {
        return Urls.combinePath(path, WILDCARDS_ANY_PATH);
    }

}
