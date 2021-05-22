package io.zero88.qwe.certificate;

import io.zero88.qwe.certificate.Certificate.KeyPairCertificate;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.JksOptions;
import io.vertx.core.net.KeyCertOptions;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Getter
@Builder
@Accessors(fluent = true)
public class JKSCert implements KeyPairCertificate {

    private final @NonNull String password;
    private final @NonNull Buffer certValue;

    @Override
    public @NonNull CertificateType getType() {
        return CertificateType.JKS;
    }

    @Override
    public Buffer privateKey() {
        return Buffer.buffer(password);
    }

    @Override
    public @NonNull KeyCertOptions toKeyCert() {
        return new JksOptions().setPassword(password()).setValue(certValue());
    }

}
