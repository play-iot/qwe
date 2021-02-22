package io.github.zero88.qwe.auth;

import java.util.Collection;
import java.util.Collections;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Getter
@SuperBuilder
@Jacksonized
@FieldNameConstants(level = AccessLevel.PRIVATE)
public final class BasicCredential extends Credential.AbstractCredential {

    @Getter
    private final String password;

    @Override
    public CredentialType getType() {
        return CredentialType.BASIC;
    }

    @Override
    public String getHeaderAuthType() {
        return "Basic";
    }

    @Override
    protected Collection<String> sensitiveFields() {
        return Collections.singleton(Fields.password);
    }

    @Override
    public String toString() {
        return super.toString() + "::Password:*****";
    }

}
