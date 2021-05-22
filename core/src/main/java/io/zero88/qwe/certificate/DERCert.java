package io.zero88.qwe.certificate;

import io.zero88.qwe.certificate.Certificate.TrustedCertificate;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.TrustOptions;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Getter
@Builder
@Accessors(fluent = true)
public class DERCert implements TrustedCertificate {

    private final @NonNull Buffer certValue;

    @Override
    public final @NonNull CertificateType getType() {
        return CertificateType.PEM_CERT;
    }

    @Override
    public @NonNull TrustOptions toTrustCert() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
