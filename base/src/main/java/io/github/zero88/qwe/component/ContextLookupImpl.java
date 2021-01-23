package io.github.zero88.qwe.component;

import java.util.HashMap;

class ContextLookupImpl extends HashMap<Class<? extends ComponentContext>, ComponentContext> implements ContextLookup {

    @Override
    @SuppressWarnings("unchecked")
    public <T extends ComponentContext> T query(Class<T> aClass) {
        return (T) this.get(aClass);
    }

}
