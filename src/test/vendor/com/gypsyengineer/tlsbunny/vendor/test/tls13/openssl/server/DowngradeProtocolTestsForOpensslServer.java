package com.gypsyengineer.tlsbunny.vendor.test.tls13.openssl.server;

import com.gypsyengineer.tlsbunny.vendor.test.tls13.common.server.DowngradeProtocolTestsForServer;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.Utils;
import org.junit.BeforeClass;

import static com.gypsyengineer.tlsbunny.vendor.test.tls13.openssl.OpensslServer.opensslServer;

public class DowngradeProtocolTestsForOpensslServer extends DowngradeProtocolTestsForServer  {

    @BeforeClass
    public static void setUp() throws Exception {
        server = opensslServer()
                .noTLSv13()
                .minTLSv1().maxTLSv13()
                .enableDebugOutput().enableExtDebugOutput();
        server.start();
        Utils.waitStart(server);
    }

}
