package io.github.zero88.msa.bp.exceptions.converter;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import io.github.zero88.exceptions.HiddenException;
import io.github.zero88.exceptions.SneakyErrorCodeException;
import io.github.zero88.msa.bp.dto.ErrorMessage;
import io.github.zero88.msa.bp.exceptions.BlueprintException;
import io.github.zero88.msa.bp.exceptions.ErrorCode;
import io.github.zero88.utils.Strings;
import io.reactivex.exceptions.CompositeException;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * Convert any {@code throwable} to friendly {@code blueprintException}. The converter result will be showed directly to
 * end user, any technical information will be log.
 *
 * @see ErrorMessage
 * @see BlueprintException
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public final class BlueprintExceptionConverter implements Function<Throwable, BlueprintException> {

    private static final Logger logger = LoggerFactory.getLogger(BlueprintExceptionConverter.class);
    private final boolean friendly;
    private final String overrideMsg;

    /**
     * Friendly converter for human user
     *
     * @param throwable any exception
     * @return blueprint exception
     */
    public static BlueprintException friendly(Throwable throwable) {
        return new BlueprintExceptionConverter(true, null).apply(throwable);
    }

    /**
     * Friendly converter for human user
     *
     * @param throwable   any exception
     * @param overrideMsg Override message
     * @return blueprint exception
     */
    public static BlueprintException friendly(Throwable throwable, String overrideMsg) {
        return new BlueprintExceptionConverter(true, overrideMsg).apply(throwable);
    }

    /**
     * Raw converter for system process
     *
     * @param throwable any exception
     * @return blueprint exception
     */
    public static BlueprintException from(Throwable throwable) {
        return new BlueprintExceptionConverter(false, null).apply(throwable);
    }

    @Override
    public BlueprintException apply(@NonNull Throwable throwable) {
        Throwable t = throwable;
        if (t instanceof CompositeException) {
            List<Throwable> exceptions = ((CompositeException) throwable).getExceptions();
            t = exceptions.get(exceptions.size() - 1);
        }
        if (t instanceof SneakyErrorCodeException) {
            //TODO convert SneakyError
            return apply(t.getCause());
        }
        if (t instanceof BlueprintException) {
            return overrideMsg(friendly ? convert((BlueprintException) t, true) : (BlueprintException) t);
        }
        if (t.getCause() instanceof BlueprintException) {
            // Rarely case
            logger.debug("Wrapper Exception: ", t);
            return overrideMsg(
                friendly ? convert((BlueprintException) t.getCause(), false) : (BlueprintException) t.getCause());
        }
        return convert(new BlueprintException(ErrorCode.UNKNOWN_ERROR, overrideMsg, t), false);
    }

    private BlueprintException overrideMsg(BlueprintException t) {
        if (Strings.isBlank(overrideMsg)) {
            return t;
        }
        return new BlueprintException(t.errorCode(), overrideMsg, t.getCause());
    }

    private BlueprintException convert(BlueprintException t, boolean wrapperIsblueprint) {
        final Throwable cause = t.getCause();
        final io.github.zero88.exceptions.ErrorCode code = t.errorCode();
        final String message = originMessage(code, t.getMessage());
        if (Objects.isNull(cause)) {
            return t;
        }
        if (cause instanceof IllegalArgumentException || cause instanceof NullPointerException) {
            final String msg = Strings.isBlank(cause.getMessage()) ? message : cause.getMessage();
            final Throwable rootCause = Objects.isNull(cause.getCause()) ? cause : cause.getCause();
            return new BlueprintException(ErrorCode.INVALID_ARGUMENT, msg, rootCause);
        }
        if (cause instanceof BlueprintException) {
            if (!wrapperIsblueprint) {
                return (BlueprintException) cause;
            }
            return new BlueprintException(t.errorCode(), includeCauseMessage(cause, message), cause);
        }
        return new BlueprintException(code, includeCauseMessage(cause, message), cause);
    }

    private String originMessage(io.github.zero88.exceptions.ErrorCode code, String message) {
        return Strings.isBlank(message) ? code.code() : message;
    }

    private String includeCauseMessage(Throwable cause, @NonNull String message) {
        if (Strings.isBlank(cause.getMessage())) {
            return message;
        }
        String mc = cause.getMessage().equals("null") ? cause.toString() : cause.getMessage();
        return Strings.format("{0} | Cause: {1}", message, mc);
    }

    private String includeCauseMessage(Exception cause, @NonNull String message) {
        if (cause instanceof HiddenException) {
            return message;
        }
        io.github.zero88.exceptions.ErrorCode code = cause instanceof BlueprintException
                         ? ((BlueprintException) cause).errorCode()
                         : ErrorCode.UNKNOWN_ERROR;
        String causeMsg = Objects.isNull(cause.getMessage()) ? "" : cause.getMessage();
        return Strings.format("{0} | Cause: {1} - Error Code: {2}", message, causeMsg, code.code());
    }

}
