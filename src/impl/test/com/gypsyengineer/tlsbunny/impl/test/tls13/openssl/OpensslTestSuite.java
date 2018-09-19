package com.gypsyengineer.tlsbunny.impl.test.tls13.openssl;

import com.gypsyengineer.tlsbunny.impl.test.tls13.TestForServer;
import com.gypsyengineer.tlsbunny.impl.test.tls13.Utils;
import com.gypsyengineer.tlsbunny.tls13.client.HttpsClient;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class OpensslTestSuite {

    private static OpensslServer server;

    @BeforeClass
    public static void setup() throws Exception {
        server = new OpensslServer();
        server.start();
        Utils.waitServerStart(server);
    }

    @Test
    public void httpClient() throws Exception {
        new TestForServer()
                .set(new HttpsClient())
                .set(server)
                .run();
    }

    @AfterClass
    public static void shutdown() throws Exception {
        server.close();
        Utils.waitServerStop(server);
    }
}
