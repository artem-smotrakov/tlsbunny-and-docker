package com.gypsyengineer.tlsbunny.vendor.test.tls13;

import com.gypsyengineer.tlsbunny.tls13.fuzzer.DeepHandshakeFuzzerConfigs;
import com.gypsyengineer.tlsbunny.tls13.server.HttpsServer;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import org.junit.Test;

import static com.gypsyengineer.tlsbunny.tls13.client.HttpsClient.httpsClient;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.DeepHandshakeFuzzyClient.deepHandshakeFuzzyClient;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.MultiConfigClient.multiConfigClient;
import static com.gypsyengineer.tlsbunny.tls13.server.HttpsServer.httpsServer;
import static com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup.secp256r1;

public class TestForVendorTest {

    @Test
    public void test() throws Exception {
        Config mainConfig = SystemPropertiesConfig.load();
        HttpsServer server = httpsServer();
        try (server) {
            server.set(mainConfig).set(secp256r1).neverStop().start();

            Utils.waitStart(server);

            new VendorTest()
                    .set(multiConfigClient()
                            .from(deepHandshakeFuzzyClient().of(httpsClient()))
                            .configs(DeepHandshakeFuzzerConfigs.noClientAuth(mainConfig)))
                    .set(server)
                    .run();
        }

        Utils.waitStop(server);
    }

}
