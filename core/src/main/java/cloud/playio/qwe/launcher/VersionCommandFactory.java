package cloud.playio.qwe.launcher;

import io.vertx.core.spi.launcher.DefaultCommandFactory;

public final class VersionCommandFactory extends DefaultCommandFactory<VersionCommand> {

    public VersionCommandFactory() {
        super(VersionCommand.class, VersionCommand::new);
    }

}
