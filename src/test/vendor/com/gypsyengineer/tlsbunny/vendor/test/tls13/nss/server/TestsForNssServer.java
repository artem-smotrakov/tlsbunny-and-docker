package com.gypsyengineer.tlsbunny.vendor.test.tls13.nss.server;

import com.gypsyengineer.tlsbunny.vendor.test.tls13.Utils;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.common.server.TestsForServer;
import org.junit.BeforeClass;

import static com.gypsyengineer.tlsbunny.vendor.test.tls13.nss.NssServer.nssServer;

public class TestsForNssServer extends TestsForServer {

    @BeforeClass
    public static void setUp() throws Exception {
        server = nssServer();
        server.start();
        Utils.waitStart(server);
    }

}
