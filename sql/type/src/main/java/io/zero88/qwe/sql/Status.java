package io.zero88.qwe.sql;

import io.zero88.qwe.dto.EnumType.AbstractEnumType;

public final class Status extends AbstractEnumType {

    public static final Status SUCCESS = new Status("SUCCESS");
    public static final Status FAILED = new Status("FAILED");
    public static final Status INITIAL = new Status("INITIAL");
    public static final Status UNDEFINED = new Status("UNDEFINED");

    protected Status(String type) {
        super(type);
    }

}
