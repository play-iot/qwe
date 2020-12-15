package io.github.zero88.msa.bp.exceptions.converter;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import io.github.zero88.exceptions.ErrorCode;
import io.github.zero88.exceptions.ErrorCodeException;
import io.github.zero88.exceptions.HiddenException;
import io.github.zero88.msa.bp.dto.ErrorMessage;
import io.github.zero88.msa.bp.exceptions.BlueprintException;
import io.github.zero88.utils.Strings;
import io.reactivex.exceptions.CompositeException;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * Convert any {@code throwable} to friendly {@code blueprintException}. The converter result will be showed directly to
 * end user, any technical information will be log.
 *
 * @see ErrorMessage
 * @see BlueprintException
 */
@AllArgsConstructor
public class BlueprintExceptionConverter implements Function<Throwable, BlueprintException> {

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
        final Throwable cause = t.getCause();
        if (t instanceof ErrorCodeException) {
            final ErrorCodeException e = (ErrorCodeException) t;
            if (ErrorCode.REFLECTION_ERROR.equals(e.errorCode())) {
                return apply(cause);
            }
            return overrideMsg(friendly ? convert(e, t instanceof BlueprintException) : e);
        }
        if (cause instanceof BlueprintException) {
            logger.debug("Wrapper Exception: ", t);
            final BlueprintException c = (BlueprintException) cause;
            return overrideMsg(friendly ? convert(c, false) : c);
        }
        return convert(new BlueprintException(ErrorCode.UNKNOWN_ERROR, overrideMsg, t), false);
    }

    private BlueprintException overrideMsg(ErrorCodeException t) {
        if (t instanceof BlueprintException && Strings.isBlank(overrideMsg)) {
            return (BlueprintException) t;
        }
        return new BlueprintException(t.errorCode(), Strings.fallback(overrideMsg, t.getMessage()), t.getCause());
    }

    private BlueprintException convert(ErrorCodeException t, boolean wrapperIsBlueprint) {
        final Throwable cause = t.getCause();
        final ErrorCode code = t.errorCode();
        if (Objects.isNull(cause)) {
            if (t instanceof BlueprintException) {
                return (BlueprintException) t;
            }
            return new BlueprintException(code, t.getMessage());
        }
        final String msgOrCode = originMessage(code, t.getMessage());
        if (cause instanceof IllegalArgumentException || cause instanceof NullPointerException) {
            final String msg = Strings.isBlank(cause.getMessage()) ? msgOrCode : cause.getMessage();
            final Throwable rootCause = Objects.isNull(cause.getCause()) ? cause : cause.getCause();
            return new BlueprintException(ErrorCode.INVALID_ARGUMENT, msg, rootCause);
        }
        if (cause instanceof ErrorCodeException) {
            if (!wrapperIsBlueprint && cause instanceof BlueprintException) {
                return (BlueprintException) cause;
            }
            return new BlueprintException(code, includeCauseMessage((ErrorCodeException) cause, msgOrCode), cause);
        }
        return new BlueprintException(code, includeCauseMessage(cause, msgOrCode), cause);
    }

    private String originMessage(ErrorCode code, String message) {
        return Strings.isBlank(message) ? code.code() : message;
    }

    private String includeCauseMessage(@NonNull Throwable cause, @NonNull String message) {
        if (Strings.isBlank(cause.getMessage())) {
            return message;
        }
        String mc = cause.getMessage().equals("null") ? cause.toString() : cause.getMessage();
        return Strings.format("{0} | Cause: {1}", message, mc);
    }

    private String includeCauseMessage(@NonNull ErrorCodeException cause, @NonNull String message) {
        if (ErrorCode.HIDDEN.equals(cause.errorCode()) || cause instanceof HiddenException) {
            return message;
        }
        ErrorCode code = cause.errorCode();
        String causeMsg = Objects.isNull(cause.getMessage()) ? "" : cause.getMessage();
        return Strings.format("{0} | Cause: {1} - Error Code: {2}", message, causeMsg, code.code());
    }

}
