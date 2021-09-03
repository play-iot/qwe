package io.zero88.qwe.auth;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import io.zero88.qwe.dto.JsonData;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.jackson.Jacksonized;

/**
 * Represents for a request authentication/authorization definition that is model of {@link AuthN}, {@link AuthZ} and
 * {@link AuthZContainer}
 *
 * @see ReqAuthZDefinition
 * @since 1.0.0
 */
@Jacksonized
@Builder(access = AccessLevel.PACKAGE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReqAuthDefinition implements JsonData {

    private final boolean loginRequired;
    private final List<ReqAuthZDefinition> authz;

    public static ReqAuthDefinition requireLogin() {
        return new ReqAuthDefinition(true, null);
    }

    public static ReqAuthDefinition noAuth() {
        return new ReqAuthDefinition(false, null);
    }

    public static ReqAuthDefinition authz(@NonNull List<ReqAuthZDefinition> definitions) {
        return new ReqAuthDefinition(true, definitions);
    }

    @JsonIgnore
    public boolean isAuthzRequired() {
        return authz != null;
    }

    public boolean isLoginRequired() {
        return loginRequired;
    }

    /**
     * Get a request authorization definitions
     *
     * @return the authorization definitions. It might be {@code null} if {@link #isLoginRequired()} or {@link
     *     #isAuthzRequired()} is {@code False}
     */
    public @Nullable List<ReqAuthZDefinition> getAuthz() {
        return authz;
    }

}
