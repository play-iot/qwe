package io.github.zero88.qwe.auth.certificate;

import io.github.zero88.qwe.auth.Certificate;
import io.github.zero88.qwe.auth.Certificate.KeyPairCertificate;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.KeyCertOptions;
import io.vertx.core.net.PemKeyCertOptions;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Getter
@Builder
@Accessors(fluent = true)
public class PKCS8Cert implements KeyPairCertificate {

    private final @NonNull Buffer privateKey;
    private final @NonNull Buffer certValue;

    @Override
    public final @NonNull CertificateType getType() {
        return CertificateType.PKCS8;
    }

    @Override
    public @NonNull KeyCertOptions toKeyCert() {
        return new PemKeyCertOptions().setKeyValue(privateKey()).setCertValue(certValue());
    }

}
