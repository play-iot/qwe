package io.zero88.qwe;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"unchecked", "rawtypes"})
interface ApplicationContextHolderInternal extends ApplicationContextHolder {

    static ApplicationContextHolderInternal create() {
        return new ApplicationContextHolderImpl();
    }

    void addPlugin(PluginContext context);

    void addExtension(Extension extension);

    class ApplicationContextHolderImpl implements ApplicationContextHolderInternal {

        private final Map<Class<? extends PluginContext>, PluginContext> plugins = new HashMap<>();
        private final Map<Class<? extends Extension>, Extension> extensions = new HashMap<>();

        @Override
        public <T extends PluginContext> T plugin(Class<T> pluginContextCls) {
            return (T) this.plugins.get(pluginContextCls);
        }

        @Override
        public <E extends Extension> @Nullable E getExtension(Class<E> extensionCls) {
            return (E) Optional.ofNullable(this.extensions.get(extensionCls)).orElse(null);
        }

        @Override
        public Collection<PluginContext> plugins() {
            return this.plugins.values();
        }

        @Override
        public Collection<Extension> extensions() {
            return this.extensions.values();
        }

        @Override
        public void addPlugin(PluginContext context) {
            this.plugins.put(context.getClass(), context);
        }

        @Override
        public void addExtension(Extension extension) {
            this.extensions.put(extension.getClass(), extension);
        }

    }

}
