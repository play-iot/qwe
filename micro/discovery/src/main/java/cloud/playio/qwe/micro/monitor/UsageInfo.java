package cloud.playio.qwe.micro.monitor;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import cloud.playio.qwe.dto.JsonData;

@DataObject(generateConverter = true)
public class UsageInfo implements JsonData {

    private final String id;
    private final UsageType usageType;
    private final Record record;

    public UsageInfo(String id, UsageType usageType, Record record) {
        this.id = id;
        this.usageType = usageType;
        this.record = record;
    }

    public String getId() {
        return id;
    }

    public UsageType type() {
        return usageType;
    }

    public Record getRecord() {
        return record;
    }

    public static UsageInfo parse(JsonObject body) {
        return new UsageInfo(body.getString("id"), UsageType.factory(body.getString("type")),
                             new Record(body.getJsonObject("record")));
    }

}
