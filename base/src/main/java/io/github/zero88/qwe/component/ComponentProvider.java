package io.github.zero88.qwe.component;

import java.util.function.Supplier;

/**
 * Component Provider
 *
 * @param <T> Component type
 */
public interface ComponentProvider<T extends Component> extends Supplier<T> {

    Class<T> unitClass();

}
