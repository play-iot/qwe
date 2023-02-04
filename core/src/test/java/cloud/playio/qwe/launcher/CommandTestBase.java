package cloud.playio.qwe.launcher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.CountDownLatch;
import java.util.logging.LogManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import io.github.zero88.utils.FileUtils;
import io.vertx.core.Vertx;
import cloud.playio.qwe.QWELauncher;
import cloud.playio.qwe.TestHelper;

public class CommandTestBase {

    protected static final PrintStream originalOutputPrintStream = System.out;
    protected static final PrintStream originalErrorPrintStream = System.err;

    protected ByteArrayOutputStream output;
    protected ByteArrayOutputStream error;

    protected PrintStream os;
    protected PrintStream err;
    protected QWELauncher cli;

    @BeforeEach
    public void setUp() throws IOException {
        System.getProperties().stringPropertyNames().forEach(prop -> {
            if (prop.startsWith("vertx.")) {
                System.clearProperty(prop);
            }
        });

        output = new ByteArrayOutputStream();
        error = new ByteArrayOutputStream();
        cli = new QWELauncher();
        // We need to reset the log configuration to recreate the logger
        // Indeed print stream may have been cached.
        LogManager.getLogManager().reset();
    }

    @AfterEach
    public void tearDown() {
        stopRecord();
        if (os != null) {
            FileUtils.silentClose(os);
        }
        if (err != null) {
            FileUtils.silentClose(err);
        }
        FileUtils.silentClose(output);
        FileUtils.silentClose(error);
    }

    public void startRecord() {
        os = new PrintStream(output);
        err = new PrintStream(error);

        System.setOut(os);
        System.setErr(err);
    }

    public void stopRecord() {
        if (System.out != originalOutputPrintStream) {
            System.setOut(originalOutputPrintStream);
        }
        if (System.err != originalErrorPrintStream) {
            System.setErr(originalErrorPrintStream);
        }
    }

    protected void close(Vertx vertx) {
        if (vertx == null) {
            return;
        }
        CountDownLatch latch = new CountDownLatch(1);
        vertx.close(ar -> latch.countDown());
        TestHelper.waitTimeout(10, latch);
    }

}
