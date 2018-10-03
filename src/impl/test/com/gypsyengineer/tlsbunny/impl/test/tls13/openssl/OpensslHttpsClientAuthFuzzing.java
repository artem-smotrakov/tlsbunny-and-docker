package com.gypsyengineer.tlsbunny.impl.test.tls13.openssl;

import com.gypsyengineer.tlsbunny.impl.test.tls13.TestForServer;
import com.gypsyengineer.tlsbunny.impl.test.tls13.Utils;
import com.gypsyengineer.tlsbunny.tls13.client.FuzzyHttpsClient;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static com.gypsyengineer.tlsbunny.impl.test.tls13.Utils.checkForASanFindings;
import static com.gypsyengineer.tlsbunny.tls13.client.FuzzyClient.certificateConfigs;
import static com.gypsyengineer.tlsbunny.tls13.client.FuzzyClient.certificateVerifyConfigs;

public class OpensslHttpsClientAuthFuzzing {

    private static OpensslServer server;
    private static Config mainConfig = SystemPropertiesConfig.load();

    @BeforeClass
    public static void setUp() throws Exception {
        server = new OpensslServer();
        server.dockerEnv("OPTIONS", "-Verify 0 -CAfile certs/root_cert.pem");
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
                        .set(certificateConfigs(mainConfig)))
                .set(server)
                .run();
    }

    @Test
    public void certificateVerify() throws Exception {
        new TestForServer()
                .set(new FuzzyHttpsClient()
                        .set(certificateVerifyConfigs(mainConfig)))
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
