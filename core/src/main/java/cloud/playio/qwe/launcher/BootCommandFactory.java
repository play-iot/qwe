package cloud.playio.qwe.launcher;

import io.vertx.core.spi.launcher.DefaultCommandFactory;

public final class BootCommandFactory extends DefaultCommandFactory<BootCommand> {

    public BootCommandFactory() {
        super(BootCommand.class, BootCommand::new);
    }

}
