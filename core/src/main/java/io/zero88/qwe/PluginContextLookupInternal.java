package io.zero88.qwe;

import java.util.Collection;
import java.util.HashMap;

interface PluginContextLookupInternal extends PluginContextLookup {

    static PluginContextLookupInternal create() {
        return new PluginContextLookupImpl();
    }

    Collection<PluginContext> list();

    void add(PluginContext context);

    class PluginContextLookupImpl extends HashMap<Class<? extends PluginContext>, PluginContext>
        implements PluginContextLookup, PluginContextLookupInternal {

        @Override
        @SuppressWarnings("unchecked")
        public <T extends PluginContext> T query(Class<T> aClass) {
            return (T) this.get(aClass);
        }

        @Override
        public Collection<PluginContext> list() {
            return this.values();
        }

        @Override
        public void add(PluginContext context) {
            this.put(context.getClass(), context);
        }

    }

}
