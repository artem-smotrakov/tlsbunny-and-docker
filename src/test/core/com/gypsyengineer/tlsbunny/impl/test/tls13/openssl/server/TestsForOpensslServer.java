package com.gypsyengineer.tlsbunny.impl.test.tls13.openssl.server;

import com.gypsyengineer.tlsbunny.impl.test.tls13.ImplTest;
import com.gypsyengineer.tlsbunny.impl.test.tls13.Utils;
import com.gypsyengineer.tlsbunny.impl.test.tls13.openssl.OpensslServer;
import com.gypsyengineer.tlsbunny.tls13.client.*;
import com.gypsyengineer.tlsbunny.tls13.client.ccs.CCSAfterHandshake;
import com.gypsyengineer.tlsbunny.tls13.client.ccs.InvalidCCS;
import com.gypsyengineer.tlsbunny.tls13.client.ccs.MultipleCCS;
import com.gypsyengineer.tlsbunny.tls13.client.ccs.StartWithCCS;
import com.gypsyengineer.tlsbunny.tls13.connection.check.AlertCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.check.AllFailedCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.check.ExceptionCheck;
import com.gypsyengineer.tlsbunny.tls13.struct.ContentType;
import org.junit.*;

import java.io.IOException;
import java.net.SocketException;

import static com.gypsyengineer.tlsbunny.impl.test.tls13.Utils.checkForASanFindings;
import static com.gypsyengineer.tlsbunny.impl.test.tls13.openssl.OpensslServer.opensslServer;

public class TestsForOpensslServer {

    private static OpensslServer server;

    @BeforeClass
    public static void setUp() throws Exception {
        server = opensslServer();
        server.start();
        Utils.waitStart(server);
    }

    @Before
    public void serverReady() throws IOException, InterruptedException {
        Utils.waitServerReady(server);
    }

    @Test
    public void httpClient() throws Exception {
        new ImplTest()
                .set(new HttpsClient())
                .set(server)
                .run();
    }

    @Test
    public void ccsAfterHandshake() throws Exception {
        new ImplTest()
                .set(new CCSAfterHandshake())
                .set(server)
                .run();
    }

    @Test
    public void doubleClientHello() throws Exception {
        new ImplTest()
                .set(new DoubleClientHello())
                .set(server)
                .run();
    }

    @Test
    @Ignore
    /**
     * The test is ignored because it fails since the server doesn't send an alert.
     *
     * The TLS 1.3 spec says the following:
     *
     *      Implementations MUST NOT send None-length fragments of Handshake
     *      types, even if those fragments contain padding.
     *
     *  https://tools.ietf.org/html/draft-ietf-tls-tls13-28#section-5.1
     *
     *  Should it expect an alert them
     *  after sending an empty TLSPlaintext message of handshake type?
     */
    public void startWithTLSPlaintextWithHandshake() throws Exception {
        new ImplTest()
                .set(new StartWithEmptyTLSPlaintext().set(ContentType.handshake))
                .set(server)
                .run();
    }

    @Test
    @Ignore
    /**
     * The test is ignored because it fails since the server doesn't send an alert.
     *
     * The TLS 1.3 spec says the following:
     *
     *    A change_cipher_spec record received before the first ClientHello message
     *    or after the peer's Finished message MUST be treated as an unexpected record type
     *
     *  https://tools.ietf.org/html/draft-ietf-tls-tls13-28#section-5
     *
     *  Is it a minor bug in OpenSSL?
     */
    public void startWithTLSPlaintextWithCCS() throws Exception {
        new ImplTest()
                .set(new StartWithEmptyTLSPlaintext().set(ContentType.change_cipher_spec))
                .set(server)
                .run();
    }

    @Test
    public void startWithTLSPlaintextWithApplicationData() throws Exception {
        new ImplTest()
                .set(new StartWithEmptyTLSPlaintext().set(ContentType.application_data))
                .set(server)
                .run();
    }

    @Test
    public void startWithTLSPlaintextWithAlert() throws Exception {
        new ImplTest()
                .set(new StartWithEmptyTLSPlaintext().set(ContentType.alert))
                .set(server)
                .run();
    }

    @Test
    public void startWithCCS() throws Exception {
        new ImplTest()
                .set(new StartWithCCS())
                .set(server)
                .run();
    }

    @Test
    public void multipleCCS() throws Exception {
        new ImplTest()
                .set(new MultipleCCS())
                .set(server)
                .run();
    }

    @Test
    public void invalidCCS() throws Exception {
        // sometimes OpenSSL sends an alert, but sometimes it just closes the connection
        // it may be a little bug, needs more investigation

        new ImplTest()
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
        new ImplTest()
                .set(new ECDHEStrictValidation())
                .set(server)
                .run();
    }

    @Test
    public void manyGroupsInClientHello() throws Exception {
        new ImplTest()
                .set(new ManyGroupsInClientHello())
                .set(server)
                .run();
    }

    @Test
    public void startWithServerHello() throws Exception {
        new ImplTest()
                .set(new StartWithServerHello())
                .set(server)
                .run();
    }

    @Test
    public void startWithFinished() throws Exception {
        new ImplTest()
                .set(new StartWithFinished())
                .set(server)
                .run();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        server.close();
        Utils.waitStop(server);
        checkForASanFindings(server.output());
    }
}
