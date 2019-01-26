package com.gypsyengineer.tlsbunny.tls13.client;

import com.gypsyengineer.tlsbunny.TestUtils;
import com.gypsyengineer.tlsbunny.TestUtils.FakeTestAnalyzer;
import com.gypsyengineer.tlsbunny.tls13.fuzzer.DeepHandshakeFuzzerConfigs;
import com.gypsyengineer.tlsbunny.tls13.fuzzer.DeepHandshakeFuzzyClient;
import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.fuzzer.DeepHandshakeFuzzer;
import com.gypsyengineer.tlsbunny.tls13.server.HttpsServer;
import com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType;
import com.gypsyengineer.tlsbunny.tls13.utils.FuzzerConfig;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.Output;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import org.junit.Test;

import static com.gypsyengineer.tlsbunny.tls13.server.HttpsServer.httpsServer;
import static com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType.*;
import static org.junit.Assert.*;

public class DeepHandshakeFuzzyClientTest {

    private static final int start = 21;
    private static final int end = 22;
    private static final int parts = 1;

    // number of connections during fuzzing (we don't forget a smoke test)
    private static final int n = end - start + 2;

    private Config clientConfig = SystemPropertiesConfig.load();

    @Test
    public void noClientAuth() throws Exception {
        test(minimized(DeepHandshakeFuzzerConfigs.noClientAuth(clientConfig)));
    }

    public void test(FuzzerConfig[] configs) throws Exception {
        for (FuzzerConfig config : configs) {
            test(config);
        }
    }

    public void test(FuzzerConfig fuzzerConfig) throws Exception {
        Output serverOutput = Output.console("server");
        Output clientOutput = Output.console("client");

        assertTrue(fuzzerConfig.factory() instanceof DeepHandshakeFuzzer);
        DeepHandshakeFuzzer deepHandshakeFuzzer = (DeepHandshakeFuzzer) fuzzerConfig.factory();

        Config serverConfig = SystemPropertiesConfig.load();

        HttpsServer server = httpsServer()
                .set(serverConfig)
                .set(serverOutput)
                .maxConnections(n);

        DeepHandshakeFuzzyClient deepHandshakeFuzzyClient =
                new DeepHandshakeFuzzyClient(new HttpsClient(), fuzzerConfig, clientOutput);

        FakeTestAnalyzer analyzer = new FakeTestAnalyzer();
        analyzer.set(clientOutput);

        try (deepHandshakeFuzzyClient; server; clientOutput; serverOutput) {
            server.start();

            fuzzerConfig.port(server.port());

            deepHandshakeFuzzyClient
                    .set(fuzzerConfig)
                    .set(clientOutput)
                    .set(analyzer)
                    .connect();
        }

        assertArrayEquals(
                deepHandshakeFuzzer.targeted(),
                new HandshakeType[] { client_hello, finished });

        analyzer.run();
        assertEquals(n, analyzer.engines().length);
        for (Engine engine : analyzer.engines()) {
            assertFalse(engine.context().hasAlert());
        }
    }

    private static FuzzerConfig[] minimized(FuzzerConfig[] configs) {
        FuzzerConfig config = configs[0];
        config.startTest(start);
        config.endTest(end);
        config.parts(parts);

        DeepHandshakeFuzzer factory = (DeepHandshakeFuzzer) config.factory();
        factory.fuzzer(new TestUtils.FakeFlipFuzzer());

        return new FuzzerConfig[] { config };
    }
}
