package com.gypsyengineer.tlsbunny.impl.test.tls13.openssl.client;

import com.gypsyengineer.tlsbunny.impl.test.tls13.ImplTest;
import com.gypsyengineer.tlsbunny.impl.test.tls13.Utils;
import com.gypsyengineer.tlsbunny.tls13.client.HttpsClient;
import com.gypsyengineer.tlsbunny.tls13.fuzzer.DeepHandshakeFuzzyClient;
import com.gypsyengineer.tlsbunny.tls13.fuzzer.MultiThreadedClient;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static com.gypsyengineer.tlsbunny.impl.test.tls13.Utils.checkForASanFindings;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.DeepHandshakeFuzzyClient.deepHandshakeFuzzyClient;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.MutatedClient.*;

public class OpensslHttpsClientFuzzing {

    private static OpensslServer server;
    private static Config mainConfig = SystemPropertiesConfig.load();

    @BeforeClass
    public static void setUp() throws Exception {
        server = new OpensslServer();
        server.start();
        Utils.waitStart(server);
    }

    @Before
    public void serverReady() throws IOException, InterruptedException {
        Utils.waitServerReady(server);
    }

    @Test
    public void ccs() throws Exception {
        new ImplTest()
                .set(new MultiThreadedClient()
                        .set(ccsConfigs(mainConfig)))
                .set(server)
                .run();
    }

    @Test
    public void tlsPlaintext() throws Exception {
        new ImplTest()
                .set(new MultiThreadedClient()
                        .set(tlsPlaintextConfigs(mainConfig)))
                .set(server)
                .run();
    }

    @Test
    public void handshake() throws Exception {
        new ImplTest()
                .set(new MultiThreadedClient()
                        .set(handshakeConfigs(mainConfig)))
                .set(server)
                .run();
    }

    @Test
    public void clientHello() throws Exception {
        new ImplTest()
                .set(new MultiThreadedClient()
                        .set(clientHelloConfigs(mainConfig)))
                .set(server)
                .run();
    }

    @Test
    public void finished() throws Exception {
        new ImplTest()
                .set(new MultiThreadedClient()
                        .set(finishedConfigs(mainConfig)))
                .set(server)
                .run();
    }

    @Test
    public void cipherSuites() throws Exception {
        new ImplTest()
                .set(new MultiThreadedClient()
                        .set(cipherSuitesConfigs(mainConfig)))
                .set(server)
                .run();
    }

    @Test
    public void extensionVector() throws Exception {
        new ImplTest()
                .set(new MultiThreadedClient()
                        .set(extensionVectorConfigs(mainConfig)))
                .set(server)
                .run();
    }

    @Test
    public void legacyCompressionMethods() throws Exception {
        new ImplTest()
                .set(new MultiThreadedClient()
                        .set(legacyCompressionMethodsConfigs(mainConfig)))
                .set(server)
                .run();
    }

    @Test
    public void legacySessionId() throws Exception {
        new ImplTest()
                .set(new MultiThreadedClient()
                        .set(legacySessionIdConfigs(mainConfig)))
                .set(server)
                .run();
    }

    @Test
    public void deepHandshakeFuzzer() throws Exception {
        new ImplTest()
                .set(new MultiThreadedClient()
                        .set((config, output) -> deepHandshakeFuzzyClient(new HttpsClient(), config, output))
                        .set(DeepHandshakeFuzzyClient.noClientAuth(mainConfig)))
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
