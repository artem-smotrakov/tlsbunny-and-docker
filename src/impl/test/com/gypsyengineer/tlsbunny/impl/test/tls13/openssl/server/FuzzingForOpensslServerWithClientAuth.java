package com.gypsyengineer.tlsbunny.impl.test.tls13.openssl.server;

import com.gypsyengineer.tlsbunny.impl.test.tls13.ImplTest;
import com.gypsyengineer.tlsbunny.impl.test.tls13.Utils;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static com.gypsyengineer.tlsbunny.impl.test.tls13.Utils.checkForASanFindings;
import static com.gypsyengineer.tlsbunny.impl.test.tls13.openssl.server.OpensslServer.opensslServer;
import static com.gypsyengineer.tlsbunny.tls13.client.HttpsClientAuth.httpsClientAuth;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.MultiConfigClient.multiConfigClient;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.MutatedClient.mutatedClient;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.MutatedConfigs.certificateConfigs;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.MutatedConfigs.certificateVerifyConfigs;

public class FuzzingForOpensslServerWithClientAuth {

    private static OpensslServer server;
    private static Config mainConfig = SystemPropertiesConfig.load();

    @BeforeClass
    public static void setUp() throws Exception {
        server = opensslServer();
        server.dockerEnv("options", "-Verify 0 -CAfile certs/root_cert.pem");
        server.start();
        Utils.waitStart(server);
    }

    @Before
    public void serverReady() throws IOException, InterruptedException {
        Utils.waitServerReady(server);
    }

    @Test
    public void certificate() throws Exception {
        new ImplTest()
                .set(multiConfigClient()
                        .of(mutatedClient().of(httpsClientAuth()))
                        .configs(certificateConfigs(mainConfig)))
                .set(server)
                .run();
    }

    @Test
    public void certificateVerify() throws Exception {
        new ImplTest()
                .set(multiConfigClient()
                        .of(mutatedClient().of(httpsClientAuth()))
                        .configs(certificateVerifyConfigs(mainConfig)))
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
