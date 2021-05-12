package io.zero88.qwe.dto;

import java.io.Serializable;
import java.util.function.Function;

import io.zero88.qwe.exceptions.QWEException;
import io.zero88.qwe.exceptions.ErrorCode;
import io.zero88.qwe.exceptions.QWEExceptionConverter;
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
    private QWEException throwable;
    private io.github.zero88.exceptions.ErrorCode code;
    private String message;

    private ErrorMessage(@NonNull QWEException throwable) {
        this.throwable = throwable;
        this.code = throwable.errorCode();
        this.message = throwable.getMessage();
    }

    private ErrorMessage(@NonNull io.github.zero88.exceptions.ErrorCode code, String message) {
        this.code = code;
        this.message = message;
    }

    private ErrorMessage(@NonNull ErrorCode code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ErrorMessage parse(@NonNull Throwable throwable) {
        return parse(throwable, QWEExceptionConverter::friendly);
    }

    public static ErrorMessage parse(@NonNull Throwable throwable, @NonNull Function<Throwable, QWEException> c) {
        return new ErrorMessage(c.apply(throwable));
    }

    public static ErrorMessage parse(@NonNull io.github.zero88.exceptions.ErrorCode code, String message) {
        return new ErrorMessage(code, message);
    }

    @JsonCreator
    public static ErrorMessage parse(@NonNull @JsonProperty("code") String code,
                                     @JsonProperty("message") String message) {
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
