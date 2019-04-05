package com.gypsyengineer.tlsbunny.vendor.test.tls13.wolfssl.server;

import com.gypsyengineer.tlsbunny.vendor.test.tls13.Utils;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.common.server.FuzzingForServer;
import org.junit.BeforeClass;

import static com.gypsyengineer.tlsbunny.vendor.test.tls13.wolfssl.WolfsslServer.wolfsslServer;

public class FuzzingForWolfsslServer extends FuzzingForServer {

    @BeforeClass
    public static void setUp() throws Exception {
        server = wolfsslServer();
        server.start();
        Utils.waitStart(server);
    }

}
