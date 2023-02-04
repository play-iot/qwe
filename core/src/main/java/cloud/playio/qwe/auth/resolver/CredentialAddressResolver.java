package cloud.playio.qwe.auth.resolver;

import cloud.playio.qwe.auth.Credential;

import lombok.NonNull;

public interface CredentialAddressResolver<T extends Credential> {

    @NonNull String resolveAddress(@NonNull String address, @NonNull T credential);

    @NonNull String resolveHeader(@NonNull T credential);

}
