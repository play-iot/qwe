package io.zero88.qwe.dto;

import java.util.Objects;
import java.util.Optional;

import io.github.zero88.exceptions.ErrorCode;
import io.github.zero88.utils.Strings;
import io.zero88.qwe.QWEConverter;
import io.zero88.qwe.exceptions.QWEException;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;

/**
 * @see ErrorMessage
 * @see QWEException
 */
@Builder(access = AccessLevel.PRIVATE)
public final class ErrorMessageConverter implements QWEConverter<ErrorMessage, QWEException> {

    private final ErrorCode code;
    private final String overrideMsg;

    @Override
    public Class<ErrorMessage> fromClass() {
        return ErrorMessage.class;
    }

    @Override
    public Class<QWEException> toClass() {
        return QWEException.class;
    }

    public QWEException from(@NonNull ErrorMessage error) {
        if (Objects.nonNull(error.getThrowable())) {
            return error.getThrowable();
        }
        String msg = Strings.isBlank(overrideMsg)
                     ? error.getMessage()
                     : Strings.format("{0} | Error: {1}", overrideMsg, error.getCode());
        return new QWEException(Optional.ofNullable(code).orElse(error.getCode()), msg);
    }

    @Override
    public ErrorMessage to(QWEException exception) {
        return new ErrorMessage(exception);
    }

    public static QWEException convert(@NonNull ErrorMessage error) {
        return ErrorMessageConverter.builder().build().from(error);
    }

    public static QWEException override(@NonNull ErrorMessage error, ErrorCode errorCode, String overrideMsg) {
        return ErrorMessageConverter.builder().code(errorCode).overrideMsg(overrideMsg).build().from(error);
    }

}
