package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import com.gypsyengineer.tlsbunny.TestUtils.*;
import com.gypsyengineer.tlsbunny.tls13.client.Client;
import com.gypsyengineer.tlsbunny.tls13.client.HttpsClient;
import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.server.Server;
import com.gypsyengineer.tlsbunny.tls13.utils.FuzzerConfig;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.Output;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import org.junit.Test;

import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Configs.*;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Configs.legacyCompressionMethodsConfigs;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.Configs.legacySessionIdConfigs;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.MutatedServer.mutatedServer;
import static com.gypsyengineer.tlsbunny.tls13.server.HttpsServer.httpsServer;
import static org.junit.Assert.*;

public class MutatedServerTest {

    private static final int start = 2;
    private static final int end = 3;
    private static final int parts = 1;

    // number of connections during fuzzing
    private static final int n = end - start + 1;

    private Config serverConfig = SystemPropertiesConfig.load();

    @Test
    public void tlsPlaintext() throws Exception {
        test(minimized(tlsPlaintextConfigs(serverConfig)));
    }

    @Test
    public void handshake() throws Exception {
        test(minimized(handshakeConfigs(serverConfig)));
    }

    @Test
    public void clientHello() throws Exception {
        test(minimized(clientHelloConfigs(serverConfig)));
    }

    @Test
    public void ccs() throws Exception {
        test(minimized(ccsConfigs(serverConfig)));
    }

    @Test
    public void finished() throws Exception {
        test(minimized(finishedConfigs(serverConfig)));
    }

    @Test
    public void cipherSuites() throws Exception {
        test(minimized(cipherSuitesConfigs(serverConfig)));
    }

    @Test
    public void extensionVector() throws Exception {
        test(minimized(extensionVectorConfigs(serverConfig)));
    }

    @Test
    public void legacySessionId() throws Exception {
        test(minimized(legacySessionIdConfigs(serverConfig)));
    }

    @Test
    public void legacyCompressionMethods() throws Exception {
        test(minimized(legacyCompressionMethodsConfigs(serverConfig)));
    }

    public void test(FuzzerConfig[] configs) throws Exception {
        for (FuzzerConfig config : configs) {
            test(config);
        }
    }

    public void test(FuzzerConfig fuzzerConfig) throws Exception {
        Output serverOutput = new Output("server");
        Output clientOutput = new Output("client");

        Config clientConfig = SystemPropertiesConfig.load();

        Server server = mutatedServer(httpsServer(), fuzzerConfig).set(serverOutput);

        Client client = new HttpsClient()
                .set(clientConfig)
                .set(clientOutput);

        try (client; server; clientOutput; serverOutput) {
            server.start();

            clientConfig.port(server.port());

            for (int i = 0; i < n; i++) {
                client.connect();
            }
        }

        assertEquals(n, client.engines().length);
        for (Engine engine : client.engines()) {
            assertFalse(engine.context().hasAlert());
        }

        assertEquals(n, server.engines().length);
        for (Engine engine : server.engines()) {
            assertFalse(engine.context().hasAlert());
        }
    }

    private static FuzzerConfig[] minimized(FuzzerConfig[] configs) {
        FuzzerConfig config = configs[0];
        config.startTest(start);
        config.endTest(end);
        config.parts(parts);

        if (config.factory() instanceof MutatedStructFactory) {
            MutatedStructFactory factory = (MutatedStructFactory) config.factory();
            factory.fuzzer(new FakeFlipFuzzer());
        }

        if (config.factory() instanceof LegacySessionIdFuzzer) {
            LegacySessionIdFuzzer factory = (LegacySessionIdFuzzer) config.factory();
            factory.fuzzer(new FakeVectorFuzzer());
        }

        if (config.factory() instanceof LegacyCompressionMethodsFuzzer) {
            LegacyCompressionMethodsFuzzer factory = (LegacyCompressionMethodsFuzzer) config.factory();
            factory.fuzzer(new FakeCompressionMethodFuzzer());
        }

        if (config.factory() instanceof CipherSuitesFuzzer) {
            CipherSuitesFuzzer factory = (CipherSuitesFuzzer) config.factory();
            factory.fuzzer(new FakeCipherSuitesFuzzer());
        }

        if (config.factory() instanceof ExtensionVectorFuzzer) {
            ExtensionVectorFuzzer factory = (ExtensionVectorFuzzer) config.factory();
            factory.fuzzer(new FakeExtensionVectorFuzzer());
        }

        return new FuzzerConfig[] { config };
    }
}
