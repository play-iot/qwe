package io.zero88.qwe.component;

import java.nio.file.Path;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Component context after deployment
 *
 * @see Component
 */
@SuppressWarnings("rawtypes")
public interface ComponentContext {

    /**
     * Create default component context
     *
     * @param aClass    an associate componentClass class
     * @param dataDir   a current application data dir
     * @param sharedKey a key to access shared data from {@code Application}
     * @param deployId  a deployment id
     * @return ComponentContext
     */
    static ComponentContext create(Class<? extends Component> aClass, Path dataDir, String sharedKey, String deployId) {
        return new DefaultComponentContext(aClass, dataDir, sharedKey, deployId);
    }

    @NonNull Class<? extends Component> componentClz();

    @NonNull Path dataDir();

    @NonNull String sharedKey();

    @NonNull String deployId();

    @Getter
    @Accessors(fluent = true)
    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    class DefaultComponentContext implements ComponentContext {

        private final Class<? extends Component> componentClz;
        private final Path dataDir;
        private final String sharedKey;
        private final String deployId;

        protected DefaultComponentContext(@NonNull ComponentContext ctx) {
            this(ctx.componentClz(), ctx.dataDir(), ctx.sharedKey(), ctx.deployId());
        }

    }

}
