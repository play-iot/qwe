package io.zero88.qwe.launcher;

import io.vertx.core.spi.launcher.DefaultCommandFactory;

public final class RunCommandFactory extends DefaultCommandFactory<RunCommand> {

    public RunCommandFactory() {
        super(RunCommand.class, RunCommand::new);
    }

}
