package com.gypsyengineer.tlsbunny.vendor.test.tls13.openssl.server;

import com.gypsyengineer.tlsbunny.vendor.test.tls13.common.server.DowngradeProtocolTestsForServer;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.Utils;
import org.junit.BeforeClass;

import static com.gypsyengineer.tlsbunny.vendor.test.tls13.openssl.OpensslServer.opensslServer;

public class DowngradeProtocolTestsForOpensslServer extends DowngradeProtocolTestsForServer  {

    @BeforeClass
    public static void setUp() throws Exception {
        server = opensslServer();
        server.dockerEnv(
                "options",
                "-min_protocol TLSv1 -max_protocol TLSv1.3 -debug -tlsextdebug");
        server.start();
        Utils.waitStart(server);
    }

}
