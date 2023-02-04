package cloud.playio.qwe.http.server.handler;

public interface AuthRequestDispatcher<I, R> extends RequestDispatcher<I, R> {

    AuthInterceptor authInterceptor();
}
