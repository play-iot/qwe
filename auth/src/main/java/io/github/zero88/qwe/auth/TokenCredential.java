package io.github.zero88.qwe.auth;

import java.util.Collection;
import java.util.Collections;

import lombok.AccessLevel;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Getter
@SuperBuilder
@Jacksonized
@FieldNameConstants(level = AccessLevel.PRIVATE)
public final class TokenCredential extends Credential.AbstractCredential {

    private final String token;
    @Default
    private final String headerAuthType = "Bearer";

    @Override
    public CredentialType getType() {
        return CredentialType.TOKEN;
    }

    @Override
    public String toString() {
        return super.toString() + "::Token: ******************************";
    }

    @Override
    protected Collection<String> sensitiveFields() {
        return Collections.singleton(Fields.token);
    }

}
