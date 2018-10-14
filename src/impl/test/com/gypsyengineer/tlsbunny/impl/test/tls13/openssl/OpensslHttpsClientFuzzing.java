package com.gypsyengineer.tlsbunny.impl.test.tls13.openssl;

import com.gypsyengineer.tlsbunny.impl.test.tls13.TestForServer;
import com.gypsyengineer.tlsbunny.impl.test.tls13.Utils;
import com.gypsyengineer.tlsbunny.tls13.client.fuzzer.FuzzyHttpsClient;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static com.gypsyengineer.tlsbunny.impl.test.tls13.Utils.checkForASanFindings;
import static com.gypsyengineer.tlsbunny.tls13.client.fuzzer.FuzzyClient.*;

public class OpensslHttpsClientFuzzing {

    private static OpensslServer server;
    private static Config mainConfig = SystemPropertiesConfig.load();

    @BeforeClass
    public static void setUp() throws Exception {
        server = new OpensslServer();
        server.start();
        Utils.waitServerStart(server);
    }

    @Before
    public void serverReady() throws IOException, InterruptedException {
        Utils.waitServerReady(server);
    }

    @Test
    public void ccs() throws Exception {
        new TestForServer()
                .set(new FuzzyHttpsClient()
                        .set(ccsConfigs(mainConfig)))
                .set(server)
                .run();
    }

    @Test
    public void tlsPlaintext() throws Exception {
        new TestForServer()
                .set(new FuzzyHttpsClient()
                        .set(tlsPlaintextConfigs(mainConfig)))
                .set(server)
                .run();
    }

    @Test
    public void handshake() throws Exception {
        new TestForServer()
                .set(new FuzzyHttpsClient()
                        .set(handshakeConfigs(mainConfig)))
                .set(server)
                .run();
    }

    @Test
    public void clientHello() throws Exception {
        new TestForServer()
                .set(new FuzzyHttpsClient()
                        .set(clientHelloConfigs(mainConfig)))
                .set(server)
                .run();
    }

    @Test
    public void finished() throws Exception {
        new TestForServer()
                .set(new FuzzyHttpsClient()
                        .set(finishedConfigs(mainConfig)))
                .set(server)
                .run();
    }

    @Test
    public void cipherSuites() throws Exception {
        new TestForServer()
                .set(new FuzzyHttpsClient()
                        .set(cipherSuitesConfigs(mainConfig)))
                .set(server)
                .run();
    }

    @Test
    public void extensionVector() throws Exception {
        new TestForServer()
                .set(new FuzzyHttpsClient()
                        .set(extensionVectorConfigs(mainConfig)))
                .set(server)
                .run();
    }

    @Test
    public void legacyCompressionMethods() throws Exception {
        new TestForServer()
                .set(new FuzzyHttpsClient()
                        .set(legacyCompressionMethodsConfigs(mainConfig)))
                .set(server)
                .run();
    }

    @Test
    public void legacySessionId() throws Exception {
        new TestForServer()
                .set(new FuzzyHttpsClient()
                        .set(legacySessionIdConfigs(mainConfig)))
                .set(server)
                .run();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        server.close();
        Utils.waitServerStop(server);
        checkForASanFindings(server.output());
    }
}
