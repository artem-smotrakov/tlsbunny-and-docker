package com.gypsyengineer.tlsbunny.impl.test.tls13.openssl;

import com.gypsyengineer.tlsbunny.impl.test.tls13.TestForServer;
import com.gypsyengineer.tlsbunny.impl.test.tls13.Utils;
import com.gypsyengineer.tlsbunny.tls13.client.fuzzer.FuzzyHttpsClient;
import com.gypsyengineer.tlsbunny.tls13.utils.FuzzerConfig;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static com.gypsyengineer.tlsbunny.impl.test.tls13.Utils.checkForASanFindings;
import static com.gypsyengineer.tlsbunny.tls13.client.fuzzer.FuzzyClient.*;

public class OpensslHttpsClientAuthSmokeFuzzing {

    private static final int start = 10;
    private static final int end = 15;
    private static final int parts = 1;

    private static OpensslServer server;
    private static Config mainConfig = SystemPropertiesConfig.load();

    @BeforeClass
    public static void setUp() throws Exception {
        server = new OpensslServer();
        server.dockerEnv("options", "-Verify 0 -CAfile certs/root_cert.pem");
        server.start();
        Utils.waitServerStart(server);
    }

    @Before
    public void serverReady() throws IOException, InterruptedException {
        Utils.waitServerReady(server);
    }

    @Test
    public void certificate() throws Exception {
        new TestForServer()
                .set(new FuzzyHttpsClient()
                        .set(minimized(certificateConfigs(mainConfig))))
                .set(server)
                .run();
    }

    @Test
    public void certificateVerify() throws Exception {
        new TestForServer()
                .set(new FuzzyHttpsClient()
                        .set(minimized(certificateVerifyConfigs(mainConfig))))
                .set(server)
                .run();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        server.close();
        Utils.waitServerStop(server);
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
