package com.gypsyengineer.tlsbunny.impl.test.tls13;

import com.gypsyengineer.tlsbunny.tls13.fuzzer.DeepHandshakeFuzzerConfigs;
import com.gypsyengineer.tlsbunny.tls13.server.HttpsServer;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import org.junit.Test;

import static com.gypsyengineer.tlsbunny.impl.test.tls13.openssl.client.OpensslHttpsClientSmokeFuzzing.minimized;
import static com.gypsyengineer.tlsbunny.tls13.client.HttpsClient.httpsClient;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.DeepHandshakeFuzzyClient.deepHandshakeFuzzyClient;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.MultiConfigClient.multiConfigClient;
import static com.gypsyengineer.tlsbunny.tls13.server.HttpsServer.httpsServer;
import static com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup.secp256r1;

public class ImplTestTest {

    @Test
    public void test() throws Exception {
        HttpsServer server = httpsServer()
                .set(secp256r1)
                .neverStop();
        server.start();
        Utils.waitStart(server);

        Config mainConfig = SystemPropertiesConfig.load();

        try (server) {
            new ImplTest()
                    .set(multiConfigClient()
                            .of(deepHandshakeFuzzyClient().of(httpsClient()))
                            .configs(minimized(DeepHandshakeFuzzerConfigs.noClientAuth(mainConfig))))
                    .set(server)
                    .run();
        }

        Utils.waitStop(server);
    }

}
