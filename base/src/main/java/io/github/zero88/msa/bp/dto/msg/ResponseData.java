package io.github.zero88.msa.bp.dto.msg;

import io.github.zero88.msa.bp.dto.msg.DataTransferObject.AbstractDTO;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class ResponseData extends AbstractDTO {

    //    @Getter
    //    private HttpResponseStatus status = HttpResponseStatus.OK;
    //
    //    public ResponseData(JsonObject headers, JsonObject body) {
    //        super(headers, body);
    //    }
    //
    //    public static ResponseData from(@NonNull EventMessage message) {
    //        ResponseData responseData = new ResponseData();
    //        responseData.setHeaders(new JsonObject().put("status", message.getStatus())
    //                                                .put("action", message.getAction())
    //                                                .put("prevAction", message.getPrevAction()));
    //        if (message.isError()) {
    //            return responseData.setBody(message.getError().toJson());
    //        }
    //        return responseData.setBody(message.getData());
    //    }
    //
    //    public static ResponseData noContent() {
    //        return new ResponseData().setStatus(HttpResponseStatus.NO_CONTENT);
    //    }
    //
    //    public ResponseData setStatus(HttpResponseStatus status) {
    //        this.status = status;
    //        return this;
    //    }
    //
    //    @JsonIgnore
    //    public ResponseData setStatus(int status) {
    //        this.status = HttpResponseStatus.valueOf(status);
    //        return this;
    //    }
    //
    //    @JsonIgnore
    //    public boolean isError() {
    //        return Objects.nonNull(this.status) && this.status.code() >= 400;
    //    }
}
