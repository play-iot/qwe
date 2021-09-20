package io.zero88.qwe.http.server.upload;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import io.github.zero88.repl.ReflectionClass;
import io.github.zero88.utils.FileUtils;
import io.github.zero88.utils.Strings;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.eventbus.EventAction;
import io.zero88.qwe.eventbus.EventBusClient;
import io.zero88.qwe.eventbus.EventMessage;
import io.zero88.qwe.eventbus.EventPattern;
import io.zero88.qwe.http.HttpUtils.HttpHeaderUtils;
import io.zero88.qwe.http.server.handler.RestEventRequestDispatcher;

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Only override it if any performance issue
 */
public class UploadFileHandler implements RestEventRequestDispatcher {

    @Getter
    @Accessors(fluent = true)
    private EventBusClient transporter;
    private String sharedKey;
    private String address;
    private Path uploadDir;

    public static UploadFileHandler create(String handlerClass) {
        if (Strings.isBlank(handlerClass) || UploadFileHandler.class.getName().equals(handlerClass)) {
            return new UploadFileHandler();
        }
        return Objects.requireNonNull(ReflectionClass.createObject(handlerClass));
    }

    public UploadFileHandler setup(String sharedKey, String address, Path uploadDir) {
        this.sharedKey = sharedKey;
        this.address = address;
        this.uploadDir = uploadDir;
        return this;
    }

    @Override
    public void handle(RoutingContext context) {
        if (context.fileUploads().isEmpty()) {
            context.response().setStatusCode(HttpResponseStatus.BAD_REQUEST.code()).end();
            return;
        }
        transporter = EventBusClient.create(SharedDataLocalProxy.create(context.vertx(), sharedKey));
        JsonObject data = new JsonObject();
        context.fileUploads().forEach(fileUpload -> data.put(fileUpload.name(), extractFileInfo(fileUpload)));
        data.put("attributes", HttpHeaderUtils.serializeHeaders(context.request().formAttributes()));
        dispatch(context, address, EventPattern.REQUEST_RESPONSE, EventMessage.initial(EventAction.CREATE, data));
    }

    private JsonObject extractFileInfo(FileUpload fileUpload) {
        return new JsonObject().put("fileName", fileUpload.fileName())
                               .put("file", uploadDir.relativize(Paths.get(fileUpload.uploadedFileName())).toString())
                               .put("ext", FileUtils.getExtension(fileUpload.fileName()))
                               .put("charset", fileUpload.charSet())
                               .put("contentType", fileUpload.contentType())
                               .put("size", fileUpload.size());
    }

}
