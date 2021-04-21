package io.zero88.qwe.http.server.download;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.zero88.qwe.exceptions.NotFoundException;
import io.zero88.qwe.http.server.HttpLogSystem.DownloadLogSystem;
import io.github.zero88.utils.Reflections.ReflectionClass;
import io.github.zero88.utils.Strings;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Download file handler helps extract {@code URL path} to {@code file id} then do some stuffs (e.g: find record in
 * Database) to lookup actual file.
 */
@Getter
@RequiredArgsConstructor
public abstract class DownloadFileHandler implements Handler<RoutingContext>, DownloadLogSystem {

    private final Logger logger = LoggerFactory.getLogger(DownloadFileHandler.class);
    private final String downloadPath;
    private final Path downloadDir;

    public static DownloadFileHandler create(String handlerClass, String downloadPath, @NonNull Path downloadDir) {
        if (Strings.isBlank(handlerClass) || DownloadFileHandler.class.getName().equals(handlerClass)) {
            return new DownloadFileHandler(downloadPath, downloadDir) {
                protected Path getFilePath(String fileId) {
                    return downloadDir.resolve(fileId);
                }
            };
        }
        Map<Class, Object> inputs = new LinkedHashMap<>();
        inputs.put(String.class, downloadPath);
        inputs.put(Path.class, downloadDir);
        return ReflectionClass.createObject(handlerClass, inputs);
    }

    @Override
    public void handle(RoutingContext context) {
        final String fileId = context.request().path().replaceFirst(downloadPath, "");
        final Path filePath = getFilePath(fileId);
        if (filePath.toFile().exists()) {
            context.response()
                   .setChunked(true)
                   .setStatusCode(HttpResponseStatus.OK.code())
                   .sendFile(filePath.toString(), ar -> {
                       if (!ar.succeeded()) {
                           logger.warn(decor("Something wrong when sending file"), ar.cause());
                       }
                   });
        } else {
            throw new NotFoundException(decor("Not found file: " + fileId + " in system"));
        }
    }

    protected abstract Path getFilePath(String fileId);

}
