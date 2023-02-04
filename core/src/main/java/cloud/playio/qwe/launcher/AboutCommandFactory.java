package cloud.playio.qwe.launcher;

import io.vertx.core.spi.launcher.DefaultCommandFactory;

public final class AboutCommandFactory extends DefaultCommandFactory<AboutCommand> {

    public AboutCommandFactory() {
        super(AboutCommand.class, AboutCommand::new);
    }

}
