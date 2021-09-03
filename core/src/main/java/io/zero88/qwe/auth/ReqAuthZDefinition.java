package io.zero88.qwe.auth;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import io.zero88.qwe.dto.JsonData;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

/**
 * Represents for the request authorization definition that is model of {@link AuthZ}
 *
 * @since 1.0.0
 */
@Getter
@Builder
@Jacksonized
public final class ReqAuthZDefinition implements JsonData {

    private final @Nullable List<String> allowRoles;
    private final @Nullable List<String> allowPerms;
    private final @Nullable List<String> allowGroups;
    private final @Nullable String customAccessRule;

}
