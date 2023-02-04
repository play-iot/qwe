package cloud.playio.qwe.http.server.download;

import java.util.Objects;

import io.github.zero88.repl.ReflectionClass;
import io.github.zero88.utils.Strings;
import cloud.playio.qwe.eventbus.EventMessage;
import cloud.playio.qwe.http.server.HttpSystem.DownloadSystem;
import cloud.playio.qwe.http.server.handler.EventBusProxyDispatcher;
import cloud.playio.qwe.http.server.handler.ResponseFileInterceptor;
import cloud.playio.qwe.http.server.handler.ResponseInterceptor;

/**
 * Download file handler helps extract {@code URL path} to {@code file id} then do some stuffs (e.g: find record in
 * Database) to lookup actual file.
 */
public interface DownloadFileHandler extends EventBusProxyDispatcher<DownloadFile>, DownloadSystem {

    static DownloadFileHandler create(String downloadHandlerCls) {
        if (Strings.isBlank(downloadHandlerCls) || DownloadFileHandler.class.getName().equals(downloadHandlerCls)) {
            return new DownloadFileHandlerImpl();
        }
        return Objects.requireNonNull(ReflectionClass.createObject(downloadHandlerCls));
    }

    @Override
    default ResponseInterceptor<DownloadFile> responseInterceptor() {
        return new ResponseFileInterceptor();
    }

    @Override
    default DownloadFile convertResponse(EventMessage resp) {
        return resp.parseAndGetData(DownloadFile.class);
    }

}
