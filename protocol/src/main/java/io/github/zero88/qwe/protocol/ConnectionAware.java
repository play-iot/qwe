package io.github.zero88.qwe.protocol;

import io.github.zero88.qwe.auth.Certificate;
import io.github.zero88.qwe.auth.Credential;

import lombok.NonNull;

/**
 * Keeps connection information that can be extended at runtime with other protocol.
 *
 * @see Protocol
 */
public interface ConnectionAware {

    /**
     * Protocol
     *
     * @return protocol
     */
    @NonNull Protocol protocol();

    /**
     * Connect strategy
     *
     * @return strategy
     * @see ConnectStrategy
     */
    @NonNull ConnectStrategy strategy();

    /**
     * Target address
     *
     * @return address
     */
    @NonNull String address();

    /**
     * Defines a protocol for establishing authenticated and encrypted links between networked
     * services/computers/devices/machines
     *
     * @return true if using {@code SSL/TLS}
     */
    boolean useSSL();

    /**
     * Defines login credential to access target address
     *
     * @return login credential
     * @see Credential
     */
    Credential credential();

    /**
     * Defines {@code SSL/TLS certificate} to access target address if {@link #useSSL()} is {@code true}
     *
     * @return certificate
     * @see Certificate
     */
    Certificate certificate();

}
