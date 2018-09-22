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

public class OpensslTestSuite {

    private static OpensslServer server;

    @BeforeClass
    public static void setUp() throws Exception {
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

    @Test
    public void ccsAfterHandshake() throws Exception {
        new TestForServer()
                .set(new CCSAfterHandshake())
                .set(server)
                .run();
    }

    @Test
    public void doubleClientHello() throws Exception {
        new TestForServer()
                .set(new DoubleClientHello())
                .set(server)
                .run();
    }

    @Test
    public void startWithTLSPlaintextWithHandshake() throws Exception {
        new TestForServer()
                .set(new StartWithEmptyTLSPlaintext().set(ContentType.handshake))
                .set(server)
                .run();
    }

    @Test
    public void startWithTLSPlaintextWithCCS() throws Exception {
        new TestForServer()
                .set(new StartWithEmptyTLSPlaintext().set(ContentType.change_cipher_spec))
                .set(server)
                .run();
    }

    @Test
    public void startWithTLSPlaintextWithApplicationData() throws Exception {
        new TestForServer()
                .set(new StartWithEmptyTLSPlaintext().set(ContentType.application_data))
                .set(server)
                .run();
    }

    @Test
    public void startWithTLSPlaintextWithAlert() throws Exception {
        new TestForServer()
                .set(new StartWithEmptyTLSPlaintext().set(ContentType.alert))
                .set(server)
                .run();
    }

    @Test
    public void startWithCCS() throws Exception {
        new TestForServer()
                .set(new StartWithCCS())
                .set(server)
                .run();
    }

    @Test
    public void multipleCCS() throws Exception {
        new TestForServer()
                .set(new MultipleCCS())
                .set(server)
                .run();
    }

    @Test
    public void invalidCCS() throws Exception {
        // sometimes OpenSSL sends an alert, but sometimes it just closes the connection
        // it may be a little bug, needs more investigation

        new TestForServer()
                .set(new InvalidCCS()
                        .set(new AllFailedCheck()
                                .add(new AlertCheck())
                                .add(new ExceptionCheck()
                                        .set(SocketException.class))))
                .set(server)
                .run();
    }

    @Test
    public void ecdheStrictValidation() throws Exception {
        new TestForServer()
                .set(new ECDHEStrictValidation())
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
