package io.zero88.qwe;

/**
 * Component Provider
 *
 * @param <T> Component type
 */
public interface ComponentProvider<T extends Component> {

    Class<T> componentClass();

    T provide(SharedDataLocalProxy proxy);

}
