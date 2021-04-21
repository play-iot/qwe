package io.zero88.qwe.dto.jackson;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;

import lombok.NonNull;

public final class JacksonExceptionPrettier {

    public static Throwable getCause(@NonNull Throwable throwable) {
        if (throwable instanceof JsonMappingException) {
            return getCause((JsonMappingException) throwable);
        }
        if (throwable.getCause() instanceof JsonMappingException) {
            return getCause((JsonMappingException) throwable.getCause());
        }
        return throwable;
    }

    public static Throwable getCause(@NonNull JsonMappingException throwable) {
        if (throwable instanceof InvalidDefinitionException) {
            return getCause(throwable.getCause());
        }
        return throwable;
    }

}
