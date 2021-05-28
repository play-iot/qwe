package io.zero88.qwe;

import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ContextLookup {

    /**
     * Lookup component context by its class
     *
     * @param aClass component context class
     * @param <T>    Type of component context
     * @return component context or {@code null} if not found
     */
    @Nullable <T extends ComponentContext> T query(Class<T> aClass);

}
