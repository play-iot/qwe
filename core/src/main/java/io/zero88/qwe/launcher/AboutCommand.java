package io.zero88.qwe.launcher;

import java.util.Scanner;

import io.github.zero88.utils.Strings;
import io.vertx.core.cli.CLIException;
import io.vertx.core.cli.annotations.Description;
import io.vertx.core.cli.annotations.Name;
import io.vertx.core.cli.annotations.Option;
import io.vertx.core.cli.annotations.Summary;

@Name("about")
@Summary("Show application information")
public final class AboutCommand extends VersionCommand {

    private static String banner;
    private boolean license;

    @Override
    public void run() throws CLIException {
        if (json) {
            printJsonVersion();
            return;
        }
        printBanner();
        printVersion();
        if (license) {
            System.out.println(Strings.duplicate("-", 58));
            System.out.println();
        }
    }

    @Option(longName = "license", flag = true)
    @Description("Show license")
    public void setLicense(boolean license) {
        this.license = license;
    }

    /**
     * Reads the banner from the {@code banner.txt} file.
     *
     * @return the banner
     */
    public static String getBanner() {
        if (banner != null) {
            return banner;
        }
        return banner = convert("banner.txt", is -> {
            try (Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A")) {
                return scanner.hasNext() ? scanner.next().trim() : "";
            }
        });
    }

    public static void printBanner() {
        System.out.println(getBanner());
    }

}
