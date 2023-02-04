package cloud.playio.qwe.http.server.upload;

import java.util.Collections;
import java.util.List;

import io.vertx.core.json.JsonObject;
import cloud.playio.qwe.eventbus.EBBody;
import cloud.playio.qwe.eventbus.EBContract;
import cloud.playio.qwe.utils.JsonUtils.JsonCollectors;

public final class LoggerUploadListener implements UploadListener {

    @EBContract(action = "CREATE")
    public JsonObject create(@EBBody("attributes") JsonObject attributes,
                             @EBBody("files") List<FileUploadWrapper> files) {
        logger().info(decor("Uploaded::{}"), files.stream().collect(JsonCollectors.toArray()));
        return new JsonObject().put("attributes", attributes)
                               .put("files", files.stream()
                                                  .collect(JsonCollectors.toArray(
                                                      w -> w.toJson(Collections.singleton("filePath")))));
    }

}
