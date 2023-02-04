package cloud.playio.qwe;

public interface ExtensionEntrypoint<C extends ExtensionConfig> {

    /**
     * Get extension config
     *
     * @return extension config
     */
    C extConfig();

}
