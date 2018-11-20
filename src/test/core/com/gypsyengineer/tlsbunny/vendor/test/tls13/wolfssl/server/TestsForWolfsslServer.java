package com.gypsyengineer.tlsbunny.vendor.test.tls13.wolfssl.server;

import com.gypsyengineer.tlsbunny.vendor.test.tls13.common.server.TestsForServer;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.Utils;
import org.junit.BeforeClass;

import static com.gypsyengineer.tlsbunny.vendor.test.tls13.wolfssl.WolfsslServer.wolfsslServer;

public class TestsForWolfsslServer extends TestsForServer {

    @BeforeClass
    public static void setUp() throws Exception {
        server = wolfsslServer();
        server.start();
        Utils.waitStart(server);
    }

}
