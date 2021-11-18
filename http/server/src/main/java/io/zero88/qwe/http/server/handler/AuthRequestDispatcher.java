package io.zero88.qwe.http.server.handler;

public interface AuthRequestDispatcher<I, R> extends RequestDispatcher<I, R> {

    AuthInterceptor authInterceptor();
}
