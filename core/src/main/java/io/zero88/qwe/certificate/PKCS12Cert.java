package io.zero88.qwe.certificate;

import io.zero88.qwe.certificate.Certificate.KeyPairCertificate;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.KeyCertOptions;
import io.vertx.core.net.PfxOptions;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Getter
@Builder
@Accessors(fluent = true)
public class PKCS12Cert implements KeyPairCertificate {

    private final @NonNull String password;
    private final @NonNull Buffer certValue;

    @Override
    public final @NonNull CertificateType getType() {
        return CertificateType.PKCS12;
    }

    @Override
    public Buffer privateKey() {
        return Buffer.buffer(password);
    }

    @Override
    public @NonNull KeyCertOptions toKeyCert() {
        return new PfxOptions().setPassword(password()).setValue(certValue);
    }

}
