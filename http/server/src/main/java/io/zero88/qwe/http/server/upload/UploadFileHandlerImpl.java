package io.zero88.qwe.http.server.upload;

import java.nio.file.Path;

import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.auth.ReqAuthDefinition;
import io.zero88.qwe.auth.UserInfo;
import io.zero88.qwe.dto.JsonData;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.eventbus.DeliveryEvent;
import io.zero88.qwe.eventbus.EventBusClient;
import io.zero88.qwe.eventbus.EventMessage;
import io.zero88.qwe.http.server.handler.AuthInterceptor;
import io.zero88.qwe.http.server.handler.RequestDataInterceptor;
import io.zero88.qwe.utils.JsonUtils.JsonCollectors;

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Only override it if any performance issue
 */
public final class UploadFileHandlerImpl implements UploadFileHandler {

    @Getter
    @Accessors(fluent = true)
    private AuthInterceptor authInterceptor;
    private String sharedKey;
    private DeliveryEvent deliveryEvent;
    private Path uploadDir;
    private FileUploadPredicate predicate;

    public UploadFileHandler setup(String sharedKey, ReqAuthDefinition authDefinition, DeliveryEvent deliveryEvent,
                                   Path uploadDir, FileUploadPredicate predicate) {
        this.authInterceptor = AuthInterceptor.create(authDefinition);
        this.sharedKey = sharedKey;
        this.deliveryEvent = deliveryEvent;
        this.uploadDir = uploadDir;
        this.predicate = predicate;
        return this;
    }

    @Override
    public RequestDataInterceptor validator() {
        return RequestDataInterceptor.createForUpload().andThen((context, requestData) -> {
            if (context.fileUploads().isEmpty()) {
                throw new IllegalArgumentException("Missing upload file(s)");
            }
            if (predicate.nbOfFiles() > 0 && context.fileUploads().size() > predicate.nbOfFiles()) {
                throw new IllegalArgumentException("Limit " + predicate.nbOfFiles() + " upload file(s)");
            }
            return requestData;
        });
    }

    @Override
    public Future<EventMessage> proceed(RoutingContext context, UserInfo userInfo, RequestData reqData) {
        reqData.body()
               .put("files", context.fileUploads()
                                    .stream()
                                    .map(fileUpload -> FileUploadWrapper.create(uploadDir, fileUpload))
                                    .filter(wrapper -> predicate.test(reqData, wrapper))
                                    .collect(JsonCollectors.toArray(JsonData::toJson)));
        return doDispatch(EventBusClient.create(SharedDataLocalProxy.create(context.vertx(), sharedKey)),
                          deliveryEvent.getAddress(), deliveryEvent.getPattern(),
                          EventMessage.initial(deliveryEvent.getAction(), reqData).setUserInfo(userInfo));
    }

}
