package io.github.zero88.qwe.protocol;

import io.github.zero88.qwe.dto.EnumType;
import io.github.zero88.qwe.dto.EnumType.AbstractEnumType;

import com.fasterxml.jackson.annotation.JsonCreator;

public final class ConnectStrategy extends AbstractEnumType {

    private ConnectStrategy(String type) {
        super(type);
    }

    public static final ConnectStrategy FAILED_FAST = new ConnectStrategy("FAILED_FAST");
    public static final ConnectStrategy BACKGROUND = new ConnectStrategy("BACKGROUND");

    @JsonCreator
    public static ConnectStrategy factory(String type) {
        return EnumType.factory(type, ConnectStrategy.class, true);
    }

}
