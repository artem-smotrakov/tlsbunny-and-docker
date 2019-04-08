package com.gypsyengineer.tlsbunny.vendor.test.tls13.picotls.server;

import com.gypsyengineer.tlsbunny.vendor.test.tls13.Utils;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.common.server.TestsForServer;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static com.gypsyengineer.tlsbunny.vendor.test.tls13.openssl.OpensslServer.opensslServer;
import static com.gypsyengineer.tlsbunny.vendor.test.tls13.picotls.PicotlsServer.picotlsServer;

public class TestsForPicotlsServer extends TestsForServer {

    @BeforeClass
    public static void setUp() throws Exception {
        server = picotlsServer();
        server.start();
        Utils.waitStart(server);
    }

    /**
     * The test is ignored because picotls server doesn't send an alert
     * if a ClientHello contains an invalid max_fragment_length extension.
     *
     * Looks like a bug in picotls.
     */
    @Test
    @Override
    @Ignore
    public void invalidMaxFragmentLength() {}

}
