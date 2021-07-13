package io.zero88.qwe.micro.httpevent;

import java.util.Objects;

import io.vertx.core.http.HttpMethod;
import io.zero88.qwe.dto.JsonData;
import io.zero88.qwe.event.EventAction;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
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
// FIXME Why dont include capturePath and regexPath in equals
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
    private final String regexPath;

    @JsonProperty("method")
    public String method() {
        return this.getMethod().name();
    }

    public static class Builder {

        private String servicePath;

        public Builder servicePath(String servicePath) {
            this.servicePath = servicePath;
            return this;
        }

        public EventMethodMapping build() {
            return build(new HttpPathRule());
        }

        public EventMethodMapping build(@NonNull HttpPathRule rule) {
            capturePath = rule.createCapture(method, action, servicePath, capturePath);
            if (Objects.nonNull(capturePath) && Objects.isNull(regexPath)) {
                regexPath = rule.createRegex(capturePath);
            }
            return new EventMethodMapping(action, method, capturePath, regexPath);
        }

    }

}
