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

    Collection<PluginContext> plugins();

    Collection<Extension> extensions();

    void add(PluginContext context);

    void addExtension(Extension extension);

    class ApplicationContextHolderImpl implements ApplicationContextHolderInternal {

        private final Map<Class<? extends PluginContext>, PluginContext> plugins = new HashMap<>();
        private final Map<Class<? extends Extension>, Extension> extensions = new HashMap<>();

        @Override
        public <T extends PluginContext> T plugin(Class<T> pluginContextCls) {
            return (T) this.plugins.get(pluginContextCls);
        }

        @Override
        public <EE extends ExtensionEntrypoint> @Nullable EE extension(Class<? extends Extension> extensionCls) {
            return (EE) Optional.ofNullable(this.extensions.get(extensionCls)).map(Extension::entrypoint).orElse(null);
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
        public void add(PluginContext context) {
            this.plugins.put(context.getClass(), context);
        }

        @Override
        public void addExtension(Extension extension) {
            this.extensions.put(extension.getClass(), extension);
        }

    }

}
