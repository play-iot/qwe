package io.zero88.qwe.http.server.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public interface RequestInterceptor extends Handler<RoutingContext> {}
