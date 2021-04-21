package io.zero88.qwe.exceptions;

import java.util.Objects;

import io.zero88.qwe.dto.EnumType;
import io.zero88.qwe.dto.EnumType.AbstractEnumType;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.NonNull;

@Getter
public final class ErrorCode extends AbstractEnumType implements io.github.zero88.exceptions.ErrorCode, EnumType {

    public static final ErrorCode DESIRED_ERROR = new ErrorCode("DESIRED_ERROR");
    public static final ErrorCode ALREADY_EXIST = new ErrorCode("ALREADY_EXIST");
    public static final ErrorCode NOT_FOUND = new ErrorCode("NOT_FOUND");
    public static final ErrorCode SERVICE_ERROR = new ErrorCode("SERVICE_ERROR");
    public static final ErrorCode EVENT_ERROR = new ErrorCode("EVENT_ERROR");

    private ErrorCode(String code) {
        super(code);
    }

    @Override
    @JsonValue
    public @NonNull String code() {
        return type();
    }

    public static ErrorCode parse(String code) {
        return EnumType.factory(code, ErrorCode.class, true);
    }

    public int hashCode() {
        return this.code().hashCode();
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof String) {
            return Objects.equals(this.code(), o);
        }
        if (!(o instanceof io.github.zero88.exceptions.ErrorCode)) {
            return false;
        }
        return Objects.equals(this.code(), ((io.github.zero88.exceptions.ErrorCode) o).code());
    }

}
