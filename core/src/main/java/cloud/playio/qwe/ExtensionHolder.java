package cloud.playio.qwe;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("rawtypes")
interface ExtensionHolder {

    /**
     * Get list of extensions
     *
     * @return extensions
     */
    @NotNull Collection<Extension> extensions();

    /**
     * Lookup extension by extension class
     *
     * @param extCls extension class
     * @param <E>    Type of extension
     * @return extension
     */
    @Nullable <E extends Extension> E getExtension(Class<E> extCls);

}
