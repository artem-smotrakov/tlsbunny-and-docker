package com.gypsyengineer.tlsbunny.impl.test.tls13.openssl.server;

import com.gypsyengineer.tlsbunny.impl.test.tls13.VendorTest;
import com.gypsyengineer.tlsbunny.impl.test.tls13.Utils;
import com.gypsyengineer.tlsbunny.impl.test.tls13.openssl.OpensslServer;
import com.gypsyengineer.tlsbunny.tls13.fuzzer.DeepHandshakeFuzzerConfigs;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static com.gypsyengineer.tlsbunny.impl.test.tls13.Utils.checkForASanFindings;
import static com.gypsyengineer.tlsbunny.impl.test.tls13.openssl.OpensslServer.opensslServer;
import static com.gypsyengineer.tlsbunny.tls13.client.HttpsClient.httpsClient;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.DeepHandshakeFuzzyClient.deepHandshakeFuzzyClient;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.MutatedConfigs.*;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.MultiConfigClient.multiConfigClient;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.MutatedClient.mutatedClient;

public class FuzzingForOpensslServer {

    private static OpensslServer server;
    private static Config mainConfig = SystemPropertiesConfig.load();

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
    public void ccs() throws Exception {
        new VendorTest()
                .set(multiConfigClient()
                        .of(mutatedClient().of(httpsClient()))
                        .configs(ccsConfigs(mainConfig)))
                .set(server)
                .run();
    }

    @Test
    public void tlsPlaintext() throws Exception {
        new VendorTest()
                .set(multiConfigClient()
                        .of(mutatedClient().of(httpsClient()))
                        .configs(tlsPlaintextConfigs(mainConfig)))
                .set(server)
                .run();
    }

    @Test
    public void handshake() throws Exception {
        new VendorTest()
                .set(multiConfigClient()
                        .of(mutatedClient().of(httpsClient()))
                        .configs(handshakeConfigs(mainConfig)))
                .set(server)
                .run();
    }

    @Test
    public void clientHello() throws Exception {
        new VendorTest()
                .set(multiConfigClient()
                        .of(mutatedClient().of(httpsClient()))
                        .configs(clientHelloConfigs(mainConfig)))
                .set(server)
                .run();
    }

    @Test
    public void finished() throws Exception {
        new VendorTest()
                .set(multiConfigClient()
                        .of(mutatedClient().of(httpsClient()))
                        .configs(finishedConfigs(mainConfig)))
                .set(server)
                .run();
    }

    @Test
    public void cipherSuites() throws Exception {
        new VendorTest()
                .set(multiConfigClient()
                        .of(mutatedClient().of(httpsClient()))
                        .configs(cipherSuitesConfigs(mainConfig)))
                .set(server)
                .run();
    }

    @Test
    public void extensionVector() throws Exception {
        new VendorTest()
                .set(multiConfigClient()
                        .of(mutatedClient().of(httpsClient()))
                        .configs(extensionVectorConfigs(mainConfig)))
                .set(server)
                .run();
    }

    @Test
    public void legacyCompressionMethods() throws Exception {
        new VendorTest()
                .set(multiConfigClient()
                        .of(mutatedClient().of(httpsClient()))
                        .configs(legacyCompressionMethodsConfigs(mainConfig)))
                .set(server)
                .run();
    }

    @Test
    public void legacySessionId() throws Exception {
        new VendorTest()
                .set(multiConfigClient()
                        .of(mutatedClient().of(httpsClient()))
                        .configs(legacySessionIdConfigs(mainConfig)))
                .set(server)
                .run();
    }

    @Test
    public void deepHandshakeFuzzer() throws Exception {
        new VendorTest()
                .set(multiConfigClient()
                        .of(deepHandshakeFuzzyClient().of(httpsClient()))
                        .configs(DeepHandshakeFuzzerConfigs.noClientAuth(mainConfig)))
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
