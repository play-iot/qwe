package io.zero88.qwe.http;

import java.util.Objects;

import io.vertx.core.http.HttpMethod;
import io.zero88.qwe.auth.ReqAuthDefinition;
import io.zero88.qwe.dto.JsonData;
import io.zero88.qwe.eventbus.EventAction;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.jackson.Jacksonized;

/**
 * Represents for a relationship between {@code EventAction}, {@code HttpMethod} and {@code url capture path}
 *
 * @see EventAction
 * @see HttpMethod
 */
@Getter
@Jacksonized
@Builder(builderClassName = "Builder")
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class EventMethodMapping implements JsonData {

    @Include
    @NonNull
    private final EventAction action;
    @Include
    @NonNull
    private final HttpMethod method;
    private final String capturePath;
    /**
     * Optional
     */
    @Include
    private final String regexPath;
    @Default
    private final ReqAuthDefinition auth = ReqAuthDefinition.noAuth();

    @JsonProperty("method")
    public String method() {
        return getMethod().name();
    }

    public static class Builder {

        private String servicePath;

        public Builder servicePath(String servicePath) {
            this.servicePath = servicePath;
            return this;
        }

        public EventMethodMapping build() {
            final HttpPathRule rule = HttpPathRuleLoader.getInstance().get();
            capturePath = rule.createCapture(method, action, servicePath, capturePath);
            if (Objects.nonNull(capturePath) && Objects.isNull(regexPath)) {
                regexPath = rule.createRegex(capturePath);
            }
            return new EventMethodMapping(action, method, capturePath, regexPath, auth$value);
        }

    }

}
