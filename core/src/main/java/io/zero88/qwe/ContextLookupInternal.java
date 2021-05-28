package io.zero88.qwe;

import java.util.Collection;
import java.util.HashMap;

interface ContextLookupInternal extends ContextLookup {

    static ContextLookupInternal create() {
        return new ContextLookupImpl();
    }

    Collection<ComponentContext> list();

    void add(ComponentContext context);

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

}
