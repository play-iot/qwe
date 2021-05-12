package io.zero88.qwe.dto.converter;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import io.github.zero88.exceptions.ErrorCode;
import io.zero88.qwe.dto.ErrorMessage;
import io.zero88.qwe.exceptions.QWEException;
import io.github.zero88.utils.Strings;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;

/**
 * @see ErrorMessage
 * @see QWEException
 */
@SuppressWarnings("unchecked")
@Builder(access = AccessLevel.PRIVATE)
public final class ErrorMessageConverter<T extends QWEException> implements Function<ErrorMessage, T> {

    private final ErrorCode code;
    private final String overrideMsg;

    public static QWEException from(@NonNull ErrorMessage error) {
        return ErrorMessageConverter.builder().build().apply(error);
    }

    public static QWEException override(@NonNull ErrorMessage error, ErrorCode errorCode, String overrideMsg) {
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
        return (T) new QWEException(Optional.ofNullable(code).orElse(error.getCode()), msg);
    }

}
