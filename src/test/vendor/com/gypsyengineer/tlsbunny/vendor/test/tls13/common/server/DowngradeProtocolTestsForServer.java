package com.gypsyengineer.tlsbunny.vendor.test.tls13.common.server;

import com.gypsyengineer.tlsbunny.TestUtils;
import com.gypsyengineer.tlsbunny.tls13.client.HttpsClient;
import com.gypsyengineer.tlsbunny.tls13.client.downgrade.CheckDowngradeMessage;
import com.gypsyengineer.tlsbunny.tls13.client.downgrade.NoSupportedVersions;
import com.gypsyengineer.tlsbunny.tls13.server.Server;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.Utils;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.VendorTest;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion.*;

public abstract class DowngradeProtocolTestsForServer {

    protected static Server server;

    @Before
    public void serverReady() throws IOException, InterruptedException {
        Utils.waitServerReady(server);
    }

    @Test
    public void httpClient() throws Exception {
        // try to establish a normal connection
        // to check if the server works well

        new VendorTest()
                .set(new HttpsClient())
                .set(server)
                .run();
    }

    @Test
    public void checkDowngradeTLSv10() throws Exception {
        new VendorTest()
                .set(new CheckDowngradeMessage().expect(TLSv10))
                .set(server)
                .run();
    }

    @Test
    public void checkDowngradeTLSv11() throws Exception {
        new VendorTest()
                .set(new CheckDowngradeMessage().expect(TLSv11))
                .set(server)
                .run();
    }

    @Test
    public void checkDowngradeTLSv12() throws Exception {
        new VendorTest()
                .set(new CheckDowngradeMessage().expect(TLSv12))
                .set(server)
                .run();
    }

    @Test
    public void noDowngradeMessage() throws Exception {
        new VendorTest()
                .set(new CheckDowngradeMessage().expect(TLSv13))
                .set(server)
                .run();
    }

    @Test
    public void noSupportedVersions() throws Exception {
        new VendorTest()
                .set(new NoSupportedVersions())
                .set(server)
                .run();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        TestUtils.tearDown(server);
    }
}
