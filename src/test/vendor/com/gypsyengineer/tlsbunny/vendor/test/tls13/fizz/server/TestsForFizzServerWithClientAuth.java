package com.gypsyengineer.tlsbunny.vendor.test.tls13.fizz.server;

import com.gypsyengineer.tlsbunny.vendor.test.tls13.Utils;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.common.server.TestsForServerWithClientAuth;
import org.junit.BeforeClass;

import static com.gypsyengineer.tlsbunny.vendor.test.tls13.fizz.FizzServer.fizzServer;

public class TestsForFizzServerWithClientAuth extends TestsForServerWithClientAuth {

    @BeforeClass
    public static void setUp() throws Exception {
        server = fizzServer().clientAuth();
        server.start();
        Utils.waitStart(server);
    }

}
