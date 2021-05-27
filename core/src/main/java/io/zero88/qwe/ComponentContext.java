package io.zero88.qwe;

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
public interface ComponentContext extends HasVerticleName {

    /**
     * Create default component context
     *
     * @param componentName an associate component name
     * @param dataDir       a current application data dir
     * @param sharedKey     a key to access shared data from {@code Application}
     * @param deployId      a deployment id
     * @return ComponentContext
     */
    static ComponentContext create(String componentName, Path dataDir, String sharedKey, String deployId) {
        return new DefaultComponentContext(componentName, dataDir, sharedKey, deployId);
    }

    @NonNull Path dataDir();

    @NonNull String sharedKey();

    @NonNull String deployId();

    @Getter
    @Accessors(fluent = true)
    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    class DefaultComponentContext implements ComponentContext {

        private final String appName;
        private final Path dataDir;
        private final String sharedKey;
        private final String deployId;

        protected DefaultComponentContext(@NonNull ComponentContext ctx) {
            this(ctx.appName(), ctx.dataDir(), ctx.sharedKey(), ctx.deployId());
        }

    }

}
