package io.zero88.qwe.auth.resolver;

import io.zero88.qwe.auth.Credential;

import lombok.NonNull;

public interface CredentialAddressResolver<T extends Credential> {

    @NonNull String resolveAddress(@NonNull String address, @NonNull T credential);

    @NonNull String resolveHeader(@NonNull T credential);

}
