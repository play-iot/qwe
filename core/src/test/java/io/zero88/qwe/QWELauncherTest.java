package io.zero88.qwe;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.zero88.qwe.launcher.CommandTestBase;

class QWELauncherTest extends CommandTestBase {

    @Test
    @Disabled
    public void testRegularBareCommand() throws InterruptedException {
        startRecord();

        //        cli.dispatch(new String[] {"run", "io.zero88.qwe.mock.MockApplication"});
        cli.dispatch(new String[] {"-h"});
        //        TestHelper.waitTimeout(10, latch);
        Thread.sleep(10000L);
        stopRecord();
        //                assertWaitUntil(() -> error.toString().contains("A quorum has been obtained."));
        //        assertThatVertxInstanceHasBeenCreated();
        //        System.out.println(error.toString());
        //        System.out.println(output.toString());
        //        System.out.println(os.toString());
        //        System.out.println(err.toString());
        System.out.println(output.toString());
        System.out.println(err.toString());
        //        TestHelper.LOGGER.info(error.toString());
        //        stop();

        //        assertThat(error.toString())
        //            .contains("Starting clustering...")
        //            .contains("Any deploymentIDs waiting on a quorum will now be deployed");
    }

}
