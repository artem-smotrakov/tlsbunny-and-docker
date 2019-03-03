package com.gypsyengineer.tlsbunny.vendor.test.tls13.openssl.server;

import com.gypsyengineer.tlsbunny.vendor.test.tls13.Utils;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.common.server.FuzzingForServerWithClientAuth;
import org.junit.BeforeClass;

import static com.gypsyengineer.tlsbunny.vendor.test.tls13.openssl.OpensslServer.opensslServer;

public class FuzzingForOpensslServerWithClientAuth extends FuzzingForServerWithClientAuth {

    @BeforeClass
    public static void setUp() throws Exception {
        server = opensslServer().clientAuth();
        server.start();
        Utils.waitStart(server);
    }

}
