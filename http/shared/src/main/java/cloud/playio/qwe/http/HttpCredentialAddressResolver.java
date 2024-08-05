package cloud.playio.qwe.http;

import cloud.playio.qwe.auth.Credential;
import cloud.playio.qwe.auth.resolver.CredentialAddressResolver;

import lombok.NonNull;

public abstract class HttpCredentialAddressResolver<T extends Credential> implements CredentialAddressResolver<T> {

    @Override
    public @NonNull String resolveAddress(@NonNull String address, @NonNull T credential) {
        return address.replaceFirst("^((https?|wss?)://)(.+)", "$1" + this.computeUrlCredential(credential) + "$3");
    }

    protected abstract String computeUrlCredential(@NonNull T credential);

}
