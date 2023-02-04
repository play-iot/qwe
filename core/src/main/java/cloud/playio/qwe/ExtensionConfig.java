package cloud.playio.qwe;

public interface ExtensionConfig extends IConfig {

    @Override
    default Class<? extends IConfig> parent() {
        return QWEAppConfig.class;
    }

}
