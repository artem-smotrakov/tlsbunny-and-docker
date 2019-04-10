package com.gypsyengineer.tlsbunny.vendor.test.tls13.picotls.server;

import com.gypsyengineer.tlsbunny.vendor.test.tls13.Utils;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.common.server.FuzzingForServerWithClientAuth;
import org.junit.BeforeClass;

import static com.gypsyengineer.tlsbunny.vendor.test.tls13.picotls.PicotlsServer.picotlsServer;

public class FuzzingForPicotlsServerWithClientAuth extends FuzzingForServerWithClientAuth {

    @BeforeClass
    public static void setUp() throws Exception {
        server = picotlsServer().clientAuth();
        server.start();
        Utils.waitStart(server);
    }

}
