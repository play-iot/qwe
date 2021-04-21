package io.zero88.qwe.component;

@FunctionalInterface
public interface ContextLookup {

    <T extends ComponentContext> T query(Class<T> aClass);

}
