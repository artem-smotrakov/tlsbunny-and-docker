package com.gypsyengineer.tlsbunny.vendor.test.tls13.common.server;

import com.gypsyengineer.tlsbunny.tls13.client.*;
import com.gypsyengineer.tlsbunny.tls13.client.ccs.CCSAfterHandshake;
import com.gypsyengineer.tlsbunny.tls13.client.ccs.InvalidCCS;
import com.gypsyengineer.tlsbunny.tls13.client.ccs.MultipleCCS;
import com.gypsyengineer.tlsbunny.tls13.client.ccs.StartWithCCS;
import com.gypsyengineer.tlsbunny.tls13.connection.check.AlertCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.check.AllFailedCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.check.ExceptionCheck;
import com.gypsyengineer.tlsbunny.tls13.server.Server;
import com.gypsyengineer.tlsbunny.tls13.struct.ContentType;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.Utils;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.VendorTest;
import org.junit.*;

import java.io.IOException;
import java.net.SocketException;

import static com.gypsyengineer.tlsbunny.vendor.test.tls13.Utils.checkForASanFindings;

public abstract class TestsForServer {

    protected static Server server;

    @Before
    public void serverReady() throws IOException, InterruptedException {
        Utils.waitServerReady(server);
    }

    @Test
    public void httpClient() throws Exception {
        new VendorTest()
                .set(new HttpsClient())
                .set(server)
                .run();
    }

    @Test
    public void ccsAfterHandshake() throws Exception {
        new VendorTest()
                .set(new CCSAfterHandshake())
                .set(server)
                .run();
    }

    @Test
    public void doubleClientHello() throws Exception {
        new VendorTest()
                .set(new DoubleClientHello())
                .set(server)
                .run();
    }

    @Test
    public void startWithTLSPlaintextWithHandshake() throws Exception {
        new VendorTest()
                .set(new StartWithEmptyTLSPlaintext().set(ContentType.handshake))
                .set(server)
                .run();
    }

    @Test
    public void startWithTLSPlaintextWithCCS() throws Exception {
        new VendorTest()
                .set(new StartWithEmptyTLSPlaintext().set(ContentType.change_cipher_spec))
                .set(server)
                .run();
    }

    @Test
    public void startWithTLSPlaintextWithApplicationData() throws Exception {
        new VendorTest()
                .set(new StartWithEmptyTLSPlaintext().set(ContentType.application_data))
                .set(server)
                .run();
    }

    @Test
    public void startWithTLSPlaintextWithAlert() throws Exception {
        new VendorTest()
                .set(new StartWithEmptyTLSPlaintext().set(ContentType.alert))
                .set(server)
                .run();
    }

    @Test
    public void startWithCCS() throws Exception {
        new VendorTest()
                .set(new StartWithCCS())
                .set(server)
                .run();
    }

    @Test
    public void multipleCCS() throws Exception {
        new VendorTest()
                .set(new MultipleCCS())
                .set(server)
                .run();
    }

    @Test
    public void invalidCCS() throws Exception {
        new VendorTest()
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
        new VendorTest()
                .set(new ECDHEStrictValidation())
                .set(server)
                .run();
    }

    @Test
    public void weakECDHE() throws Exception {
        new VendorTest()
                .set(new WeakECDHE())
                .set(server)
                .run();
    }

    @Test
    public void manyGroupsInClientHello() throws Exception {
        new VendorTest()
                .set(new ManyGroupsInClientHello())
                .set(server)
                .run();
    }

    @Test
    public void startWithServerHello() throws Exception {
        new VendorTest()
                .set(new StartWithServerHello())
                .set(server)
                .run();
    }

    @Test
    public void startWithFinished() throws Exception {
        new VendorTest()
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
