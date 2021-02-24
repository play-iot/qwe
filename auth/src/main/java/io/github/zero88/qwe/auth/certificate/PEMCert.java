package io.github.zero88.qwe.auth.certificate;

import io.github.zero88.qwe.auth.Certificate.TrustedCertificate;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.PemTrustOptions;
import io.vertx.core.net.TrustOptions;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Getter
@Builder
@Accessors(fluent = true)
public class PEMCert implements TrustedCertificate {

    private final @NonNull Buffer certValue;

    @Override
    public final @NonNull CertificateType getType() {
        return CertificateType.PEM_CERT;
    }

    @Override
    public @NonNull TrustOptions toTrustCert() {
        return new PemTrustOptions().addCertValue(certValue());
    }

}
