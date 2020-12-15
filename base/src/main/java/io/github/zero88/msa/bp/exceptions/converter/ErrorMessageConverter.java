package io.github.zero88.msa.bp.exceptions.converter;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import io.github.zero88.msa.bp.dto.ErrorMessage;
import io.github.zero88.msa.bp.exceptions.BlueprintException;
import io.github.zero88.msa.bp.exceptions.ErrorCode;
import io.github.zero88.utils.Strings;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;

/**
 * @see ErrorMessage
 * @see BlueprintException
 */
@SuppressWarnings("unchecked")
@Builder(access = AccessLevel.PRIVATE)
public final class ErrorMessageConverter<T extends BlueprintException> implements Function<ErrorMessage, T> {

    private final io.github.zero88.exceptions.ErrorCode code;
    private final String overrideMsg;

    public static BlueprintException from(@NonNull ErrorMessage error) {
        return ErrorMessageConverter.builder().build().apply(error);
    }

    public static BlueprintException from(@NonNull ErrorMessage error, ErrorCode errorCode, String overrideMsg) {
        return ErrorMessageConverter.builder().code(errorCode).overrideMsg(overrideMsg).build().apply(error);
    }

    @Override
    public T apply(@NonNull ErrorMessage error) {
        if (Objects.nonNull(error.getThrowable())) {
            return (T) error.getThrowable();
        }
        String msg = Strings.isBlank(overrideMsg)
                     ? error.getMessage()
                     : Strings.format("{0} | Error: {1}", overrideMsg, error.getCode());
        return (T) new BlueprintException(Optional.ofNullable(code).orElse(error.getCode()), msg);
    }

}
