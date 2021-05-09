package io.zero88.qwe.event.refl;

import io.zero88.qwe.event.EBContext;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class MethodParam {

    private final String paramName;
    private final Class<?> paramClass;
    /**
     * Check whether method param is EBContext or not
     *
     * @see EBContext
     */
    private final boolean isContext;

}
