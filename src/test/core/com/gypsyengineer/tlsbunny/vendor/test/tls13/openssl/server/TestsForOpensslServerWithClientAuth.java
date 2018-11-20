package com.gypsyengineer.tlsbunny.vendor.test.tls13.openssl.server;

import com.gypsyengineer.tlsbunny.vendor.test.tls13.Utils;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.common.server.TestsForServerWithClientAuth;
import org.junit.BeforeClass;

import static com.gypsyengineer.tlsbunny.vendor.test.tls13.openssl.OpensslServer.opensslServer;

public class TestsForOpensslServerWithClientAuth extends TestsForServerWithClientAuth {

    @BeforeClass
    public static void setUp() throws Exception {
        server = opensslServer();
        server.dockerEnv("options", "-Verify 0 -CAfile certs/root_cert.pem");
        server.start();
        Utils.waitStart(server);
    }

}
