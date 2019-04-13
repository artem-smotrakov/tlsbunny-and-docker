package com.gypsyengineer.tlsbunny.vendor.test.tls13.gnutls.server;

import com.gypsyengineer.tlsbunny.vendor.test.tls13.Utils;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.common.server.TestsForServer;
import org.junit.BeforeClass;

import static com.gypsyengineer.tlsbunny.vendor.test.tls13.gnutls.server.GnutlsServer.gnutlsServer;

public class TestsForGnutlsServer extends TestsForServer {

    @BeforeClass
    public static void setUp() throws Exception {
        server = gnutlsServer();
        server.start();
        Utils.waitStart(server);
    }

}
