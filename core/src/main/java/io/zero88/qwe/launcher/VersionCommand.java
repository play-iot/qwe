package io.zero88.qwe.launcher;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import io.vertx.core.cli.CLIException;
import io.vertx.core.cli.annotations.Description;
import io.vertx.core.cli.annotations.Name;
import io.vertx.core.cli.annotations.Option;
import io.vertx.core.cli.annotations.Summary;
import io.vertx.core.spi.launcher.DefaultCommand;
import io.zero88.qwe.ApplicationVersion;

@Name("version")
@Summary("Displays the Application version")
public class VersionCommand extends DefaultCommand {

    protected boolean json;
    private static ApplicationVersion av;

    @Override
    public void run() throws CLIException {
        if (json) {
            printJsonVersion();
        } else {
            printVersion();
        }
    }

    static void printVersion() {
        System.out.println(getVersion());
    }

    static void printJsonVersion() {
        System.out.println(getVersion().toJson());
    }

    @Option(longName = "json", flag = true)
    @Description("Show version in JSON format")
    public void setJson(boolean json) {
        this.json = json;
    }

    /**
     * Reads the version from the application {@code MANIFEST.MF} file.
     *
     * @return the version
     */
    public static ApplicationVersion getVersion() {
        if (av != null) {
            return av;
        }
        final Manifest manifest = convert("META-INF/MANIFEST.MF", is -> {
            try {
                return new Manifest(is);
            } catch (IOException e) {
                throw new IllegalStateException(e.getMessage());
            }
        });
        final Attributes attrs = manifest.getMainAttributes();
        return av = ApplicationVersion.builder()
                                      .name(attrs.getValue("Application"))
                                      .title(attrs.getValue(Attributes.Name.IMPLEMENTATION_TITLE.toString()))
                                      .version(attrs.getValue(Attributes.Name.IMPLEMENTATION_VERSION.toString()))
                                      .hashVersion(attrs.getValue("Build-Hash"))
                                      .coreVersion(io.vertx.core.impl.launcher.commands.VersionCommand.getVersion())
                                      .build();
    }

    static <T> T convert(String file, Function<InputStream, T> converter) {
        try (InputStream is = AboutCommand.class.getClassLoader().getResourceAsStream(file)) {
            if (is == null) {
                throw new IllegalStateException("Cannot find " + file + " on classpath");
            }
            return converter.apply(is);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

}
