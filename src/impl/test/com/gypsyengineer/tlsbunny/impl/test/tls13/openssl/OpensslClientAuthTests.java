package com.gypsyengineer.tlsbunny.impl.test.tls13.openssl;

import com.gypsyengineer.tlsbunny.impl.test.tls13.TestForServer;
import com.gypsyengineer.tlsbunny.impl.test.tls13.Utils;
import com.gypsyengineer.tlsbunny.tls13.client.*;
import com.gypsyengineer.tlsbunny.tls13.connection.AlertCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.AllFailedCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.ExceptionCheck;
import com.gypsyengineer.tlsbunny.tls13.struct.ContentType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.SocketException;

public class OpensslClientAuthTests {

    private static OpensslServer server;

    @BeforeClass
    public static void setUp() throws Exception {
        server = new OpensslServer();
        server.dockerEnv("OPTIONS", "'-Verify 0 -CAfile certs/root_cert.pem'");
        server.start();
        Utils.waitServerStart(server);
    }

    @Test
    public void httpsClient() throws Exception {
        new TestForServer()
                .set(new ClientAuth())
                .set(server)
                .run();
    }

    // TODO: check server logs for ASan findings
    @AfterClass
    public static void tearDown() throws Exception {
        server.close();
        Utils.waitServerStop(server);
    }
}
