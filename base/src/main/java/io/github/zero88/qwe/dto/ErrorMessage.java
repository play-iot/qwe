package io.github.zero88.qwe.dto;

import java.io.Serializable;
import java.util.function.Function;

import io.github.zero88.qwe.exceptions.CarlException;
import io.github.zero88.qwe.exceptions.ErrorCode;
import io.github.zero88.qwe.exceptions.converter.CarlExceptionConverter;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

/**
 * The error message.
 */
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ErrorMessage implements Serializable, JsonData {

    @JsonIgnore
    private CarlException throwable;
    private io.github.zero88.exceptions.ErrorCode code;
    private String message;

    private ErrorMessage(@NonNull CarlException throwable) {
        this.throwable = throwable;
        this.code = throwable.errorCode();
        this.message = throwable.getMessage();
    }

    private ErrorMessage(@NonNull io.github.zero88.exceptions.ErrorCode code, String message) {
        this.code = code;
        this.message = message;
    }

    private ErrorMessage(@NonNull ErrorCode code, @NonNull String message) {
        this.code = code;
        this.message = message;
    }

    public static ErrorMessage parse(@NonNull Throwable throwable) {
        return parse(throwable, CarlExceptionConverter::friendly);
    }

    public static ErrorMessage parse(@NonNull Throwable throwable, @NonNull Function<Throwable, CarlException> c) {
        return new ErrorMessage(c.apply(throwable));
    }

    public static ErrorMessage parse(@NonNull io.github.zero88.exceptions.ErrorCode code, @NonNull String message) {
        return new ErrorMessage(code, message);
    }

    @JsonCreator
    public static ErrorMessage parse(@NonNull @JsonProperty("code") String code,
                                     @NonNull @JsonProperty("message") String message) {
        return new ErrorMessage(ErrorCode.parse(code), message);
    }

    public static ErrorMessage parse(@NonNull JsonObject error) {
        return JsonData.convert(error, ErrorMessage.class);
    }

    @Override
    public JsonObject toJson() {
        final JsonObject jsonObject = JsonData.super.toJson();
        try {
            return jsonObject.put("message", message);
        } catch (DecodeException e) {
            return jsonObject;
        }
    }

}
