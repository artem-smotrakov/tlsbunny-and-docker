package com.gypsyengineer.tlsbunny.impl.test.tls13.openssl.client;

import com.gypsyengineer.tlsbunny.impl.test.tls13.ImplTest;
import com.gypsyengineer.tlsbunny.impl.test.tls13.Utils;
import com.gypsyengineer.tlsbunny.tls13.utils.FuzzerConfig;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static com.gypsyengineer.tlsbunny.impl.test.tls13.Utils.checkForASanFindings;
import static com.gypsyengineer.tlsbunny.tls13.client.HttpsClient.httpsClient;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Configs.*;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.MultiConfigClient.multiConfigClient;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.MutatedClient.mutatedClient;

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
        new ImplTest()
                .set(multiConfigClient()
                        .of(mutatedClient().of(httpsClient()))
                        .set(mainConfig)
                        .configs(minimized(ccsConfigs())))
                .set(server)
                .run();
    }

    @Test
    public void tlsPlaintext() throws Exception {
        new ImplTest()
                .set(multiConfigClient()
                        .of(mutatedClient().of(httpsClient()))
                        .set(mainConfig)
                        .configs(minimized(tlsPlaintextConfigs())))
                .set(server)
                .run();
    }

    @Test
    public void handshake() throws Exception {
        new ImplTest()
                .set(multiConfigClient()
                        .of(mutatedClient().of(httpsClient()))
                        .set(mainConfig)
                        .configs(minimized(handshakeConfigs())))
                .set(server)
                .run();
    }

    @Test
    public void clientHello() throws Exception {
        new ImplTest()
                .set(multiConfigClient()
                        .of(mutatedClient().of(httpsClient()))
                        .set(mainConfig)
                        .configs(minimized(clientHelloConfigs())))
                .set(server)
                .run();
    }

    @Test
    public void finished() throws Exception {
        new ImplTest()
                .set(multiConfigClient()
                        .of(mutatedClient().of(httpsClient()))
                        .set(mainConfig)
                        .configs(minimized(finishedConfigs())))
                .set(server)
                .run();
    }

    @Test
    public void cipherSuites() throws Exception {
        new ImplTest()
                .set(multiConfigClient()
                        .of(mutatedClient().of(httpsClient()))
                        .set(mainConfig)
                        .configs(minimized(cipherSuitesConfigs())))
                .set(server)
                .run();
    }

    @Test
    public void extensionVector() throws Exception {
        new ImplTest()
                .set(multiConfigClient()
                        .of(mutatedClient().of(httpsClient()))
                        .set(mainConfig)
                        .configs(minimized(extensionVectorConfigs())))
                .set(server)
                .run();
    }

    @Test
    public void legacyCompressionMethods() throws Exception {
        new ImplTest()
                .set(multiConfigClient()
                        .of(mutatedClient().of(httpsClient()))
                        .set(mainConfig)
                        .configs(minimized(legacyCompressionMethodsConfigs())))
                .set(server)
                .run();
    }

    @Test
    public void legacySessionId() throws Exception {
        new ImplTest()
                .set(multiConfigClient()
                        .of(mutatedClient().of(httpsClient()))
                        .set(mainConfig)
                        .configs(minimized(legacySessionIdConfigs())))
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
