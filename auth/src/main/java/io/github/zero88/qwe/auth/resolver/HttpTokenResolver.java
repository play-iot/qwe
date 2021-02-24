package io.github.zero88.qwe.auth.resolver;

import java.util.Objects;

import io.github.zero88.qwe.auth.credential.TokenCredential;

import lombok.NonNull;

public class HttpTokenResolver extends HttpCredentialAddressResolver<TokenCredential> {

    @Override
    public @NonNull String resolveAddress(@NonNull String address, @NonNull TokenCredential credential) {
        return address.replaceFirst("^((https?|wss?)://)(.+)", "$1" + this.computeUrlCredential(credential) + "$3");
    }

    @Override
    public @NonNull String resolveHeader(@NonNull TokenCredential credential) {
        return credential.getHeaderAuthType() + " " + credential.getToken();
    }

    @Override
    protected String computeUrlCredential(@NonNull TokenCredential credential) {
        return (Objects.nonNull(credential.getUser()) ? credential.getUser() + ":" : "") + credential.getToken() + "@";
    }

}
