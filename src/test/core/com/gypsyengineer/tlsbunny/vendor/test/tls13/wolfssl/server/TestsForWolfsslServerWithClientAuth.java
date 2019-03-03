package com.gypsyengineer.tlsbunny.vendor.test.tls13.wolfssl.server;

import com.gypsyengineer.tlsbunny.vendor.test.tls13.Utils;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.common.server.TestsForServerWithClientAuth;
import org.junit.BeforeClass;

import static com.gypsyengineer.tlsbunny.vendor.test.tls13.wolfssl.WolfsslServer.wolfsslServer;

public class TestsForWolfsslServerWithClientAuth extends TestsForServerWithClientAuth {

    @BeforeClass
    public static void setUp() throws Exception {
        server = wolfsslServer().clientAuth();
        server.start();
        Utils.waitStart(server);
    }

}
