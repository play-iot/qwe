package io.zero88.qwe.auth.credential;

import java.util.Collection;
import java.util.Collections;

import io.zero88.qwe.auth.Credential.AbstractCredential;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Getter
@SuperBuilder
@Jacksonized
@FieldNameConstants(level = AccessLevel.PRIVATE)
public final class BasicCredential extends AbstractCredential {

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
    public String secretValue() {
        return getPassword();
    }

    @Override
    protected Collection<String> sensitiveFields() {
        return Collections.singleton(Fields.password);
    }

    @Override
    protected String maskSensitive() {
        return "Password[******]";
    }

}
