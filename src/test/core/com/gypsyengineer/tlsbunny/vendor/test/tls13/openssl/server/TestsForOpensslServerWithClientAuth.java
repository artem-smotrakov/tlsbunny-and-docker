package com.gypsyengineer.tlsbunny.vendor.test.tls13.openssl.server;

import com.gypsyengineer.tlsbunny.vendor.test.tls13.Utils;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.common.server.TestsForServerWithClientAuth;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.openssl.OpensslServer;
import org.junit.BeforeClass;

import static com.gypsyengineer.tlsbunny.vendor.test.tls13.openssl.OpensslServer.opensslServer;

public class TestsForOpensslServerWithClientAuth extends TestsForServerWithClientAuth {

    @BeforeClass
    public static void setUp() throws Exception {
        OpensslServer opensslServer = opensslServer();
        opensslServer.dockerEnv("options", "-Verify 0 -CAfile certs/root_cert.pem");

        server = opensslServer;
        server.start();
        Utils.waitStart(server);
    }

}
