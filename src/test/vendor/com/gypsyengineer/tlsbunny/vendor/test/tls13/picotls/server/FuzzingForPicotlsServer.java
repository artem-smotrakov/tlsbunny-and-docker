package com.gypsyengineer.tlsbunny.vendor.test.tls13.picotls.server;

import com.gypsyengineer.tlsbunny.vendor.test.tls13.Utils;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.common.server.FuzzingForServer;
import org.junit.BeforeClass;

import static com.gypsyengineer.tlsbunny.vendor.test.tls13.picotls.PicotlsServer.picotlsServer;

public class FuzzingForPicotlsServer extends FuzzingForServer {

    @BeforeClass
    public static void setUp() throws Exception {
        server = picotlsServer();
        server.start();
        Utils.waitStart(server);
    }

}
