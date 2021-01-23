package io.github.zero88.qwe.component;

import java.nio.file.Path;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Component context after deployment
 *
 * @see Component
 */
@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ComponentContext {

    private final Class<? extends Component> componentClz;
    private final Path dataDir;
    private final String sharedKey;
    private final String deployId;

}
