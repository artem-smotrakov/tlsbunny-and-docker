package com.gypsyengineer.tlsbunny.vendor.test.tls13.gnutls.server;

import com.gypsyengineer.tlsbunny.vendor.test.tls13.Utils;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.common.server.TestsForServerWithClientAuth;
import org.junit.BeforeClass;

import static com.gypsyengineer.tlsbunny.vendor.test.tls13.gnutls.GnutlsServer.gnutlsServer;

public class TestsForGnutlsServerWithClientAuth extends TestsForServerWithClientAuth {

    @BeforeClass
    public static void setUp() throws Exception {
        server = gnutlsServer().clientAuth();
        server.start();
        Utils.waitStart(server);
    }

}
