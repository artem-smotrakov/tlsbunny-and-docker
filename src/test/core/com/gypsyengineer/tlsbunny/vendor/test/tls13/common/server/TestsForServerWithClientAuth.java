package com.gypsyengineer.tlsbunny.vendor.test.tls13.common.server;

import com.gypsyengineer.tlsbunny.tls13.client.HttpsClientAuth;
import com.gypsyengineer.tlsbunny.tls13.server.Server;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.Utils;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.VendorTest;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.gypsyengineer.tlsbunny.vendor.test.tls13.Utils.checkForASanFindings;

public abstract class TestsForServerWithClientAuth {

    protected static Server server;

    // provide setUp() method!

    @Before
    public void serverReady() throws IOException, InterruptedException {
        Utils.waitServerReady(server);
    }

    @Test
    public void httpsClientAuth() throws Exception {
        new VendorTest()
                .set(new HttpsClientAuth())
                .set(server)
                .run();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        server.close();
        Utils.waitStop(server);
        checkForASanFindings(server.output());
    }
}
