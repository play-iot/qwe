package io.zero88.qwe.sql.pojos;

import io.zero88.qwe.sql.type.Label;
import io.github.zero88.jooqx.JsonRecord;

public interface HasLabel extends JsonRecord {

    Label getLabel();

    <T extends HasLabel> T setLabel(Label value);

}
