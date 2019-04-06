package com.gypsyengineer.tlsbunny.vendor.test.tls13.wolfssl.server;

import com.gypsyengineer.tlsbunny.vendor.test.tls13.common.server.TestsForServer;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.Utils;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static com.gypsyengineer.tlsbunny.vendor.test.tls13.wolfssl.WolfsslServer.wolfsslServer;

public class TestsForWolfsslServer extends TestsForServer {

    @BeforeClass
    public static void setUp() throws Exception {
        server = wolfsslServer();
        server.start();
        Utils.waitStart(server);
    }

    /**
     * The test is ignored because WolfSSL server doesn't send an alert
     * if ClientHello contains an invalid max_fragment_length extension.
     *
     * Looks like a bug in WolfSSL.
     */
    @Test
    @Override
    @Ignore
    public final void invalidMaxFragmentLength() {}

}
