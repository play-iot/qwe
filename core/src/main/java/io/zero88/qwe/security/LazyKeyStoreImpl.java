package io.zero88.qwe.security;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import io.github.zero88.utils.Strings;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.KeyCertOptions;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.core.net.PemTrustOptions;
import io.vertx.core.net.TrustOptions;
import io.zero88.qwe.exceptions.CryptoException;
import io.zero88.qwe.security.KeyEntry.KeyEntryType;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class LazyKeyStoreImpl implements LazyKeyStore {

    @Getter
    @Setter(value = AccessLevel.PROTECTED)
    private KeyStoreType type;
    @Getter
    @Setter(value = AccessLevel.PROTECTED)
    private String provider;
    @Getter
    @Setter(value = AccessLevel.PROTECTED)
    private String password;
    @Getter
    @Setter(value = AccessLevel.PROTECTED)
    private String path;
    private KeyStore keyStore;
    private KeyStore clone;

    @Override
    public LazyKeyStore init(Vertx vertx) {
        if (keyStore == null) {
            keyStore = LazyKeyStore.load(vertx, this);
        }
        return this;
    }

    @Override
    public final Collection<KeyEntry> entries() {
        try {
            final KeyStore ks = get();
            final Enumeration<String> aliases = ks.aliases();
            Set<String> tmp = new HashSet<>();
            List<KeyEntry> entries = new ArrayList<>();
            while (aliases.hasMoreElements()) {
                final String alias = aliases.nextElement();
                if (tmp.contains(alias)) {
                    continue;
                }
                for (KeyEntryType type : KeyEntryType.values()) {
                    if (ks.entryInstanceOf(alias, type.getEntryClass())) {
                        entries.add(new KeyEntry(alias, type));
                        break;
                    }
                }
                tmp.add(alias);
            }
            return entries;
        } catch (KeyStoreException e) {
            throw new CryptoException("Unable load aliases", e);
        }
    }

    @Override
    public @Nullable KeyCertOptions getKeyCert(String alias, char[] password) {
        try {
            final KeyStore ks = get();
            if (!ks.containsAlias(alias)) {
                return null;
            }
            if (!ks.isKeyEntry(alias)) {
                throw new IllegalArgumentException("Alias " + alias + " is not Key");
            }
            if (ks.entryInstanceOf(alias, KeyEntryType.SECRET_KEY.getEntryClass())) {
                throw new CryptoException("The alias [" + alias + "] is not key-pair format");
            }
            Key key = ks.getKey(alias, password);
            String pKey = formatPEM(key.getEncoded(), KeyEntryType.KEY_PAIR.getPEMType(key.getAlgorithm()));
            String cert = formatPEM(ks.getCertificate(alias).getEncoded(), KeyEntryType.CERT);
            return new PemKeyCertOptions().addKeyValue(Buffer.buffer(pKey)).addCertValue(Buffer.buffer(cert));
        } catch (KeyStoreException | UnrecoverableKeyException | NoSuchAlgorithmException | CertificateEncodingException e) {
            throw new CryptoException("Unable load key", e);
        }
    }

    @Override
    public @Nullable TrustOptions getCertificateChain(String alias) {
        try {
            Certificate[] certificates = get().getCertificateChain(alias);
            if (certificates == null) {
                return null;
            }
            PemTrustOptions trustOptions = new PemTrustOptions();
            for (Certificate cert : certificates) {
                trustOptions.addCertValue(Buffer.buffer(formatPEM(cert.getEncoded(), KeyEntryType.CERT)));
            }
            return trustOptions;
        } catch (KeyStoreException | CertificateEncodingException e) {
            throw new CryptoException("Unable load certificate chain", e);
        }
    }

    @Override
    public @Nullable TrustOptions getCertificate(String alias) {
        try {
            Certificate cert = get().getCertificate(alias);
            if (cert == null) {
                return null;
            }
            return new PemTrustOptions().addCertValue(Buffer.buffer(formatPEM(cert.getEncoded(), KeyEntryType.CERT)));
        } catch (KeyStoreException | CertificateEncodingException e) {
            throw new CryptoException("Unable load certificate", e);
        }
    }

    @Override
    public @Nullable TrustOptions getCertificates() {
        try {
            final KeyStore ks = get();
            final PemTrustOptions options = new PemTrustOptions();
            final Enumeration<String> aliases = ks.aliases();
            while (aliases.hasMoreElements()) {
                Certificate cert = ks.getCertificate(aliases.nextElement());
                if (cert == null) {
                    continue;
                }
                options.addCertValue(Buffer.buffer(formatPEM(cert.getEncoded(), KeyEntryType.CERT)));
            }
            return options.getCertValues().isEmpty() ? null : options;
        } catch (KeyStoreException | CertificateEncodingException e) {
            throw new CryptoException("Unable load certificates", e);
        }
    }

    @Override
    public final KeyStore unwrap() {
        if (get() != null && clone != null) {
            return clone;
        }
        try {
            Path f = Files.createTempFile("", ".tmp");
            System.out.println(f);
            byte[] bytes = Files.readAllBytes(Files.copy(Paths.get(getPath()), f, StandardCopyOption.REPLACE_EXISTING));
            return clone = LazyKeyStore.load(this, () -> bytes);
        } catch (IOException e) {
            throw new CryptoException("Unable clone key store", e);
        }
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        KeyStore ks = get();
        try {
            Enumeration<String> aliases = ks.aliases();
            while (aliases.hasMoreElements()) {
                final String alias = aliases.nextElement();
                final Certificate cert = ks.getCertificate(alias);
                if (cert != null) {
                    json.put(alias, new JsonObject().put("cert", formatPEM(cert.getEncoded(), KeyEntryType.CERT)));
                }
            }
            return json;
        } catch (KeyStoreException | CertificateEncodingException e) {
            throw new CryptoException("Unable serialize key store to json", e);
        }
    }

    @Override
    public JsonObject toJson(Map<String, String> aliasProtection) {
        JsonObject json = new JsonObject();
        KeyStore ks = get();
        try {
            Enumeration<String> aliases = ks.aliases();
            while (aliases.hasMoreElements()) {
                final String alias = aliases.nextElement();
                final JsonObject content = new JsonObject();
                final Certificate cert = ks.getCertificate(alias);
                if (cert != null) {
                    content.put("cert", formatPEM(cert.getEncoded(), KeyEntryType.CERT));
                }
                if (ks.isKeyEntry(alias)) {
                    if (aliasProtection.containsKey(alias)) {
                        KeyEntryType type = ks.entryInstanceOf(alias, KeyEntryType.SECRET_KEY.getEntryClass())
                                            ? KeyEntryType.SECRET_KEY
                                            : KeyEntryType.KEY_PAIR;
                        Key key = ks.getKey(alias, aliasProtection.get(alias).toCharArray());
                        content.put("private_key", formatPEM(key.getEncoded(), type.getPEMType(key.getAlgorithm())));
                    }
                }
                if (!content.isEmpty()) {
                    json.put(alias, content);
                }
            }
            return json;
        } catch (KeyStoreException | CertificateEncodingException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new CryptoException("Unable serialize key store to json", e);
        }
    }

    protected final String formatPEM(byte[] der, KeyEntryType keyEntryType) {
        return formatPEM(der, keyEntryType.getPEMType(null));
    }

    protected final String formatPEM(byte[] der, String pemType) {
        final String base64 = Base64.getEncoder().encodeToString(der);
        if (Strings.isBlank(pemType)) {
            return base64;
        }
        return "-----BEGIN " + pemType + "-----\n" + base64 + "\n-----END " + pemType + "-----\n";
    }

    protected final KeyStore get() {
        return Objects.requireNonNull(keyStore, "Uninitialized keystore");
    }

}
