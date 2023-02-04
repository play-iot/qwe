package cloud.playio.qwe.exceptions;

import java.util.Objects;

import io.github.zero88.repl.ReflectionField;
import cloud.playio.qwe.dto.EnumType;
import cloud.playio.qwe.dto.EnumType.AbstractEnumType;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.NonNull;

@Getter
public final class ErrorCode extends AbstractEnumType implements io.github.zero88.exceptions.ErrorCode, EnumType {

    public static final ErrorCode DESIRED_ERROR = new ErrorCode("DESIRED_ERROR");
    public static final ErrorCode CONFLICT_ERROR = new ErrorCode("CONFLICT_ERROR");

    public static final ErrorCode DATA_ALREADY_EXIST = new ErrorCode("DATA_ALREADY_EXIST");
    public static final ErrorCode DATA_BEING_USED = new ErrorCode("DATA_BEING_USED");
    public static final ErrorCode DATA_NOT_FOUND = new ErrorCode("DATA_NOT_FOUND");

    public static final ErrorCode ENGINE_ERROR = new ErrorCode("ENGINE_ERROR");

    public static final ErrorCode NETWORK_ERROR = new ErrorCode("NETWORK_ERROR");

    public static final ErrorCode INITIALIZER_ERROR = new ErrorCode("INITIALIZER_ERROR");

    public static final ErrorCode SERVICE_ERROR = new ErrorCode("SERVICE_ERROR");
    public static final ErrorCode SERVICE_UNAVAILABLE = new ErrorCode("SERVICE_UNAVAILABLE");
    public static final ErrorCode SERVICE_NOT_FOUND = new ErrorCode("SERVICE_NOT_FOUND");

    public static final ErrorCode SECURITY_ERROR = new ErrorCode("SECURITY_ERROR");
    public static final ErrorCode AUTHENTICATION_ERROR = new ErrorCode("AUTHENTICATION_ERROR");
    public static final ErrorCode INSUFFICIENT_PERMISSION_ERROR = new ErrorCode("INSUFFICIENT_PERMISSION_ERROR");

    public static final ErrorCode TIMEOUT_ERROR = new ErrorCode("TIMEOUT_ERROR");

    @JsonCreator
    private ErrorCode(String code) {
        super(code);
    }

    @Override
    @JsonValue
    public @NonNull String code() {
        return type();
    }

    public static ErrorCode parse(String code) {
        return ReflectionField.streamConstants(ErrorCode.class, io.github.zero88.exceptions.ErrorCode.class)
                              .filter(Objects::nonNull)
                              .filter(et -> et.code().equals(code))
                              .map(ErrorCode::wrap)
                              .findFirst()
                              .orElseGet(() -> new ErrorCode(code));
    }

    public static ErrorCode wrap(io.github.zero88.exceptions.ErrorCode code) {
        return code instanceof ErrorCode ? (ErrorCode) code : new ErrorCode(code.code());
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
