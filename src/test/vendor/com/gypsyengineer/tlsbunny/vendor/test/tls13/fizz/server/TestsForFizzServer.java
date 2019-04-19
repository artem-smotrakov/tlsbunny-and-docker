package com.gypsyengineer.tlsbunny.vendor.test.tls13.fizz.server;

import com.gypsyengineer.tlsbunny.vendor.test.tls13.Utils;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.common.server.TestsForServer;
import org.junit.BeforeClass;

import static com.gypsyengineer.tlsbunny.vendor.test.tls13.fizz.FizzServer.fizzServer;

public class TestsForFizzServer extends TestsForServer {

    @BeforeClass
    public static void setUp() throws Exception {
        server = fizzServer();
        server.start();
        Utils.waitStart(server);
    }

}
