package io.github.zero88.msa.blueprint.transport;

/**
 * Represents for Proxy service.
 *
 * @param <T> Type of {@code Transporter}
 * @see Transporter
 * @since 1.0.0
 */
public interface ProxyService<T extends Transporter> {

    /**
     * Declares transporter.
     *
     * @return the transporter
     * @since 1.0.0
     */
    T transporter();

}
