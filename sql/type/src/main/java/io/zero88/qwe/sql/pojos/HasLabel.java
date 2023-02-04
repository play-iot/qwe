package cloud.playio.qwe.sql.pojos;

import cloud.playio.qwe.sql.type.Label;
import io.github.zero88.jooqx.JsonRecord;

public interface HasLabel extends JsonRecord {

    Label getLabel();

    <T extends HasLabel> T setLabel(Label value);

}
