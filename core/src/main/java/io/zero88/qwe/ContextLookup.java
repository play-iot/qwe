package io.zero88.qwe;

@FunctionalInterface
public interface ContextLookup {

    <T extends ComponentContext> T query(Class<T> aClass);

}
