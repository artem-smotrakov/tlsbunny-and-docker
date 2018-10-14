package com.gypsyengineer.tlsbunny.impl.test.tls13.openssl;

import com.gypsyengineer.tlsbunny.impl.test.tls13.TestForServer;
import com.gypsyengineer.tlsbunny.impl.test.tls13.Utils;
import com.gypsyengineer.tlsbunny.tls13.client.HttpsClient;
import com.gypsyengineer.tlsbunny.tls13.client.downgrade.CheckDowngradeMessage;
import com.gypsyengineer.tlsbunny.tls13.client.downgrade.NoSupportedVersions;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static com.gypsyengineer.tlsbunny.impl.test.tls13.Utils.checkForASanFindings;
import static com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion.*;

public class OpensslDowngradeProtocolTests {

    private static OpensslServer server;

    @BeforeClass
    public static void setUp() throws Exception {
        server = new OpensslServer();
        server.dockerEnv(
                "options",
                "-min_protocol TLSv1 -max_protocol TLSv1.3 -debug -tlsextdebug");
        server.start();
        Utils.waitServerStart(server);
    }

    @Before
    public void serverReady() throws IOException, InterruptedException {
        Utils.waitServerReady(server);
    }

    @Test
    public void httpClient() throws Exception {
        // try to establish a normal connection
        // to check if the server works well

        new TestForServer()
                .set(new HttpsClient())
                .set(server)
                .run();
    }

    @Test
    public void checkDowngradeTLSv10() throws Exception {
        new TestForServer()
                .set(new CheckDowngradeMessage().expect(TLSv10))
                .set(server)
                .run();
    }

    @Test
    public void checkDowngradeTLSv11() throws Exception {
        new TestForServer()
                .set(new CheckDowngradeMessage().expect(TLSv11))
                .set(server)
                .run();
    }

    @Test
    public void checkDowngradeTLSv12() throws Exception {
        new TestForServer()
                .set(new CheckDowngradeMessage().expect(TLSv12))
                .set(server)
                .run();
    }

    @Test
    public void noDowngradeMessage() throws Exception {
        new TestForServer()
                .set(new CheckDowngradeMessage().expect(TLSv13))
                .set(server)
                .run();
    }

    @Test
    public void noSupportedVersions() throws Exception {
        new TestForServer()
                .set(new NoSupportedVersions())
                .set(server)
                .run();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        server.close();
        Utils.waitServerStop(server);
        checkForASanFindings(server.output());
    }
}
