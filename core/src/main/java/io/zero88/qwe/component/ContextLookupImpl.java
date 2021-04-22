package io.zero88.qwe.component;

import java.util.Collection;
import java.util.HashMap;

class ContextLookupImpl extends HashMap<Class<? extends ComponentContext>, ComponentContext>
    implements ContextLookup, ContextLookupInternal {

    @Override
    @SuppressWarnings("unchecked")
    public <T extends ComponentContext> T query(Class<T> aClass) {
        return (T) this.get(aClass);
    }

    @Override
    public Collection<ComponentContext> list() {
        return this.values();
    }

    @Override
    public void add(ComponentContext context) {
        this.put(context.getClass(), context);
    }

}
