package io.zero88.qwe;

import java.util.Arrays;
import java.util.List;

import io.vertx.core.Launcher;
import io.zero88.qwe.launcher.AboutCommand;

public class QWELauncher extends Launcher {

    private static final List<String> NO_BANNER_COMMAND = Arrays.asList("about", "version");

    static {
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
    }

    public static void main(String[] args) {
        new QWELauncher().unregister("bare").dispatch(args);
    }

    @Override
    public void execute(String command, String... cla) {
        if (command != null && !isAskingForHelp(command) && !isAskingForVersion(command) &&
            !NO_BANNER_COMMAND.contains(command) && (cla.length >= 1 && !isAskingForHelp(cla[0]))) {
            AboutCommand.printBanner();
        }
        super.execute(command, cla);
    }

}
