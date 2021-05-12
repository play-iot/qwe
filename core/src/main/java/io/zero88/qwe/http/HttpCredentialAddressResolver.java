package io.zero88.qwe.http;

import io.zero88.qwe.auth.Credential;
import io.zero88.qwe.auth.resolver.CredentialAddressResolver;

import lombok.NonNull;

public abstract class HttpCredentialAddressResolver<T extends Credential> implements CredentialAddressResolver<T> {

    @Override
    public @NonNull String resolveAddress(@NonNull String address, @NonNull T credential) {
        return address.replaceFirst("^((https?|wss?)://)(.+)", "$1" + this.computeUrlCredential(credential) + "$3");
    }

    protected abstract String computeUrlCredential(@NonNull T credential);

}
