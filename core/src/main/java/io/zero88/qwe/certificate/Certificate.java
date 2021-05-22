package io.zero88.qwe.certificate;

import io.zero88.qwe.dto.EnumType;
import io.zero88.qwe.dto.EnumType.AbstractEnumType;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.JksOptions;
import io.vertx.core.net.KeyCertOptions;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.core.net.PemTrustOptions;
import io.vertx.core.net.PfxOptions;
import io.vertx.core.net.TrustOptions;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.NonNull;

public interface Certificate {

    /**
     * Certificate type
     *
     * @return certificate type
     * @see CertificateType
     */
    @NonNull CertificateType getType();

    /**
     * Certificate private key
     *
     * @return private key. {@code Nullable} if {@link #getType()} is {@link CertificateType#PEM_CERT} or {@link
     *     CertificateType#DER_CERT}
     */
    Buffer privateKey();

    /**
     * Certificate value
     *
     * @return cert value
     */
    @NonNull Buffer certValue();

    @NonNull KeyCertOptions toKeyCert();

    @NonNull TrustOptions toTrustCert();

    interface TrustedCertificate extends Certificate {

        @Override
        default Buffer privateKey() {
            return null;
        }

        @Override
        default @NonNull KeyCertOptions toKeyCert() {
            throw new UnsupportedOperationException("Unsupported generate key cert option from TrustedCert");
        }

    }


    interface KeyPairCertificate extends Certificate {

        @Override
        default @NonNull TrustOptions toTrustCert() {
            throw new UnsupportedOperationException("Use toKeyCert instead of toTrustCert");
        }

    }


    final class CertificateType extends AbstractEnumType {

        /**
         * JKS
         *
         * @see <a href="https://docs.oracle.com/javase/7/docs/api/java/security/KeyStore.html">Javadoc KeyStore</a>
         * @see <a href="https://en.wikipedia.org/wiki/Java_KeyStore">JKS</a>
         * @see JksOptions
         */
        public static final CertificateType JKS = new CertificateType("JKS");
        /**
         * PKCS #1
         *
         * @see <a href="https://en.wikipedia.org/wiki/PKCS_1">PKCS #1</a>
         * @see PemKeyCertOptions
         */
        public static final CertificateType PKCS1 = new CertificateType("PKCS#1");
        /**
         * PKCS #8
         *
         * @see <a href="https://en.wikipedia.org/wiki/PKCS_8">PKCS #8</a>
         * @see PemKeyCertOptions
         */
        public static final CertificateType PKCS8 = new CertificateType("PKCS#8");
        /**
         * PKCS #12
         *
         * @see <a href="https://en.wikipedia.org/wiki/PKCS_12">PKCS #12</a>
         * @see PfxOptions
         */
        public static final CertificateType PKCS12 = new CertificateType("PKCS#12");

        /**
         * The {@code PEM} extension is used for different types of {@code X.509v3} files which contain {@code ASCII
         * (Base64)} armored data prefixed with a {@code -----BEGIN ...} line and end with a {@code -----END ...}.
         *
         * @see <a href="https://en.wikipedia.org/wiki/Privacy-Enhanced_Mail">Privacy-Enhanced Mail</a>
         * @see PemTrustOptions
         */
        public static final CertificateType PEM_CERT = new CertificateType("PEM_CERT");
        /**
         * The {@code DER} extension is used for binary DER encoded certificates. These files may also bear the CER or
         * the CRT extension
         */
        public static final CertificateType DER_CERT = new CertificateType("PEM_CERT");

        private CertificateType(String type) {
            super(type);
        }

        @JsonCreator
        public static CertificateType factory(String name) {
            return EnumType.factory(name, CertificateType.class, true);
        }

    }

}
