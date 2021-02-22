package io.github.zero88.qwe.auth;

import io.github.zero88.qwe.dto.EnumType;
import io.github.zero88.qwe.dto.EnumType.AbstractEnumType;

import com.fasterxml.jackson.annotation.JsonCreator;

public final class CredentialType extends AbstractEnumType {

    public static final CredentialType BASIC = new CredentialType("BASIC");
    public static final CredentialType TOKEN = new CredentialType("TOKEN");

    protected CredentialType(String type) {
        super(type);
    }

    @JsonCreator
    public static CredentialType factory(String name) {
        return EnumType.factory(name, CredentialType.class, true);
    }

}
