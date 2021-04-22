package io.zero88.qwe.exceptions.converter;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.zero88.exceptions.ErrorCode;
import io.github.zero88.exceptions.ErrorCodeException;
import io.github.zero88.exceptions.HiddenException;
import io.github.zero88.utils.Reflections.ReflectionClass;
import io.github.zero88.utils.Strings;
import io.zero88.qwe.dto.ErrorMessage;
import io.zero88.qwe.exceptions.CarlException;

import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * Convert any {@code throwable} to friendly {@code carlException}. The converter result will be showed directly to end
 * user, any technical information will be log.
 *
 * @see ErrorMessage
 * @see CarlException
 */
@AllArgsConstructor
public class CarlExceptionConverter implements Function<Throwable, CarlException> {

    private static final Logger logger = LoggerFactory.getLogger(CarlExceptionConverter.class);
    private final boolean friendly;
    private final String overrideMsg;

    /**
     * Friendly converter for human user
     *
     * @param throwable any exception
     * @return carl exception
     */
    public static CarlException friendly(Throwable throwable) {
        return new CarlExceptionConverter(true, null).apply(throwable);
    }

    /**
     * Friendly converter for human user
     *
     * @param throwable   any exception
     * @param overrideMsg Override message
     * @return carl exception
     */
    public static CarlException friendly(Throwable throwable, String overrideMsg) {
        return new CarlExceptionConverter(true, overrideMsg).apply(throwable);
    }

    /**
     * Raw converter for system process
     *
     * @param throwable any exception
     * @return carl exception
     */
    public static CarlException from(Throwable throwable) {
        return new CarlExceptionConverter(false, null).apply(throwable);
    }

    @Override
    public CarlException apply(@NonNull Throwable throwable) {
        Throwable t = throwable;
        final Class<Object> rxCompositeEx = ReflectionClass.findClass("io.reactivex.exceptions.CompositeException");
        if (Objects.nonNull(rxCompositeEx) && ReflectionClass.assertDataType(t.getClass(), rxCompositeEx)) {
            List<Throwable> exceptions = ((io.reactivex.exceptions.CompositeException) throwable).getExceptions();
            t = exceptions.get(exceptions.size() - 1);
        }
        final Throwable cause = t.getCause();
        if (t instanceof ErrorCodeException) {
            final ErrorCodeException e = (ErrorCodeException) t;
            if (ErrorCode.REFLECTION_ERROR.equals(e.errorCode())) {
                return apply(cause);
            }
            return overrideMsg(friendly ? convert(e, t instanceof CarlException) : e);
        }
        if (cause instanceof CarlException) {
            logger.debug("Wrapper Exception: ", t);
            final CarlException c = (CarlException) cause;
            return overrideMsg(friendly ? convert(c, false) : c);
        }
        return convert(new CarlException(ErrorCode.UNKNOWN_ERROR, overrideMsg, t), false);
    }

    private CarlException overrideMsg(ErrorCodeException t) {
        if (t instanceof CarlException && Strings.isBlank(overrideMsg)) {
            return (CarlException) t;
        }
        return new CarlException(t.errorCode(), Strings.fallback(overrideMsg, t.getMessage()), t.getCause());
    }

    private CarlException convert(ErrorCodeException t, boolean wrapperIsCarl) {
        final Throwable cause = t.getCause();
        final ErrorCode code = t.errorCode();
        if (Objects.isNull(cause)) {
            if (t instanceof CarlException) {
                return (CarlException) t;
            }
            return new CarlException(code, t.getMessage());
        }
        final String msgOrCode = originMessage(code, t.getMessage());
        if (cause instanceof IllegalArgumentException || cause instanceof NullPointerException) {
            final String msg = Strings.isBlank(cause.getMessage()) ? msgOrCode : cause.getMessage();
            final Throwable rootCause = Objects.isNull(cause.getCause()) ? cause : cause.getCause();
            return new CarlException(ErrorCode.INVALID_ARGUMENT, msg, rootCause);
        }
        if (cause instanceof ErrorCodeException) {
            if (!wrapperIsCarl && cause instanceof CarlException) {
                return (CarlException) cause;
            }
            return new CarlException(code, includeCauseMessage((ErrorCodeException) cause, msgOrCode), cause);
        }
        return new CarlException(code, includeCauseMessage(cause, msgOrCode), cause);
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
