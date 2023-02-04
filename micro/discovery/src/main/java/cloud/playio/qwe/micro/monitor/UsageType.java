package cloud.playio.qwe.micro.monitor;

import cloud.playio.qwe.dto.EnumType;
import cloud.playio.qwe.dto.EnumType.AbstractEnumType;

import com.fasterxml.jackson.annotation.JsonCreator;

public class UsageType extends AbstractEnumType {

    public static UsageType BIND = new UsageType("bind");
    public static UsageType RELEASE = new UsageType("release");

    private UsageType(String type) {
        super(type);
    }

    @JsonCreator
    public static UsageType factory(String name) {
        return EnumType.factory(name, UsageType.class, false);
    }

}
