package com.gypsyengineer.tlsbunny.impl.test.tls13.openssl.client;

import com.gypsyengineer.tlsbunny.impl.test.tls13.TestForServer;
import com.gypsyengineer.tlsbunny.impl.test.tls13.Utils;
import com.gypsyengineer.tlsbunny.tls13.client.fuzzer.MultiThreadedClient;
import com.gypsyengineer.tlsbunny.tls13.utils.FuzzerConfig;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static com.gypsyengineer.tlsbunny.impl.test.tls13.Utils.checkForASanFindings;
import static com.gypsyengineer.tlsbunny.tls13.client.fuzzer.MutatedClient.*;

public class OpensslHttpsClientSmokeFuzzing {

    private static final int start = 10;
    private static final int end = 15;
    private static final int parts = 1;

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
        new TestForServer()
                .set(new MultiThreadedClient()
                        .set(minimized(ccsConfigs(mainConfig))))
                .set(server)
                .run();
    }

    @Test
    public void tlsPlaintext() throws Exception {
        new TestForServer()
                .set(new MultiThreadedClient()
                        .set(minimized(tlsPlaintextConfigs(mainConfig))))
                .set(server)
                .run();
    }

    @Test
    public void handshake() throws Exception {
        new TestForServer()
                .set(new MultiThreadedClient()
                        .set(minimized(handshakeConfigs(mainConfig))))
                .set(server)
                .run();
    }

    @Test
    public void clientHello() throws Exception {
        new TestForServer()
                .set(new MultiThreadedClient()
                        .set(minimized(clientHelloConfigs(mainConfig))))
                .set(server)
                .run();
    }

    @Test
    public void finished() throws Exception {
        new TestForServer()
                .set(new MultiThreadedClient()
                        .set(minimized(finishedConfigs(mainConfig))))
                .set(server)
                .run();
    }

    @Test
    public void cipherSuites() throws Exception {
        new TestForServer()
                .set(new MultiThreadedClient()
                        .set(minimized(cipherSuitesConfigs(mainConfig))))
                .set(server)
                .run();
    }

    @Test
    public void extensionVector() throws Exception {
        new TestForServer()
                .set(new MultiThreadedClient()
                        .set(minimized(extensionVectorConfigs(mainConfig))))
                .set(server)
                .run();
    }

    @Test
    public void legacyCompressionMethods() throws Exception {
        new TestForServer()
                .set(new MultiThreadedClient()
                        .set(minimized(legacyCompressionMethodsConfigs(mainConfig))))
                .set(server)
                .run();
    }

    @Test
    public void legacySessionId() throws Exception {
        new TestForServer()
                .set(new MultiThreadedClient()
                        .set(minimized(legacySessionIdConfigs(mainConfig))))
                .set(server)
                .run();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        server.close();
        Utils.waitStop(server);
        checkForASanFindings(server.output());
    }

    private static FuzzerConfig[] minimized(FuzzerConfig[] configs) {
        for (FuzzerConfig config : configs) {
            config.startTest(start);
            config.endTest(end);
            config.parts(parts);
        }

        return configs;
    }
}
