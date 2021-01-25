package io.github.zero88.qwe.micro.http;

import java.util.Objects;

import io.github.zero88.qwe.dto.JsonData;
import io.github.zero88.qwe.event.EventAction;
import io.github.zero88.qwe.utils.HttpMethods;
import io.github.zero88.utils.Strings;
import io.vertx.core.http.HttpMethod;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.Getter;
import lombok.NonNull;

/**
 * Represents for a relationship between {@code EventAction}, {@code HttpMethod} and {@code url capture path}
 *
 * @see EventAction
 * @see HttpMethod
 */
@Getter
@Builder(builderClassName = "Builder")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = EventMethodMapping.Builder.class)
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

    @JsonPOJOBuilder(withPrefix = "")
    static class Builder {

        private String servicePath;

        Builder servicePath(String servicePath) {
            this.servicePath = servicePath;
            return this;
        }

        EventMethodMapping build() {
            boolean singular = HttpMethods.isSingular(method) && action != EventAction.GET_LIST ||
                               action == EventAction.GET_ONE;
            capturePath = Strings.isBlank(servicePath) || singular ? capturePath : servicePath;
            if (Objects.nonNull(capturePath) && Objects.isNull(regexPath)) {
                regexPath = EventMethodDefinition.toRegex(capturePath);
            }
            return new EventMethodMapping(action, method, capturePath, regexPath);
        }

    }

}
