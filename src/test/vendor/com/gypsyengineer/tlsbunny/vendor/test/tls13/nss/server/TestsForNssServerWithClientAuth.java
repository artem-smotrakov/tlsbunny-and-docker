package com.gypsyengineer.tlsbunny.vendor.test.tls13.nss.server;

import com.gypsyengineer.tlsbunny.vendor.test.tls13.Utils;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.common.server.TestsForServerWithClientAuth;
import org.junit.BeforeClass;

import static com.gypsyengineer.tlsbunny.vendor.test.tls13.nss.NssServer.nssServer;

public class TestsForNssServerWithClientAuth extends TestsForServerWithClientAuth {

    @BeforeClass
    public static void setUp() throws Exception {
        server = nssServer().clientAuth();
        server.start();
        Utils.waitStart(server);
    }

}
