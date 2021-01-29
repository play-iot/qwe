package io.github.zero88.qwe.http.server;

public interface RouterConfig {

    long MB = 1024L * 1024L;

    String basePath();

    boolean isEnabled();

}
