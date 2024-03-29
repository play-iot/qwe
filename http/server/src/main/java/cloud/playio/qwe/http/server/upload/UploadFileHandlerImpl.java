package cloud.playio.qwe.http.server.upload;

import cloud.playio.qwe.auth.ReqAuthDefinition;
import cloud.playio.qwe.dto.JsonData;
import cloud.playio.qwe.eventbus.EventMessage;
import cloud.playio.qwe.http.server.handler.EventBusProxyDispatcherImpl;
import cloud.playio.qwe.http.server.handler.HttpEBDispatcher;
import cloud.playio.qwe.http.server.handler.RequestDataInterceptor;
import cloud.playio.qwe.utils.JsonUtils.JsonCollectors;

public final class UploadFileHandlerImpl extends EventBusProxyDispatcherImpl<EventMessage>
    implements UploadFileHandler {

    private FileUploadPredicate predicate;

    public UploadFileHandler setup(HttpEBDispatcher dispatcher, ReqAuthDefinition authDefinition,
                                   FileUploadPredicate predicate) {
        setup(dispatcher, authDefinition);
        this.predicate = predicate;
        return this;
    }

    @Override
    public RequestDataInterceptor validator() {
        return RequestDataInterceptor.createForUpload().andThen((ctx, reqData) -> {
            if (ctx.fileUploads().isEmpty()) {
                throw new IllegalArgumentException("Missing upload file(s)");
            }
            if (predicate.nbOfFiles() > 0 && ctx.fileUploads().size() > predicate.nbOfFiles()) {
                throw new IllegalArgumentException("Limit " + predicate.nbOfFiles() + " upload file(s)");
            }
            reqData.body()
                   .put("files", ctx.fileUploads()
                                    .stream()
                                    .map(FileUploadWrapper::create)
                                    .filter(wrapper -> predicate.test(reqData, wrapper))
                                    .collect(JsonCollectors.toArray(JsonData::toJson)));
            return reqData;
        });
    }

}
