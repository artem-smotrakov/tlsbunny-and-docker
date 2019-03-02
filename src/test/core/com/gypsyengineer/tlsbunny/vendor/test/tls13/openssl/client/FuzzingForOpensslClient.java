package com.gypsyengineer.tlsbunny.vendor.test.tls13.openssl.client;

import com.gypsyengineer.tlsbunny.tls13.fuzzer.MutatedServer;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.VendorTest;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.openssl.OpensslClient;
import org.junit.Test;

import static com.gypsyengineer.tlsbunny.tls13.fuzzer.MutatedConfigs.ccsConfigs;
import static com.gypsyengineer.tlsbunny.tls13.server.HttpsServer.httpsServer;

public class FuzzingForOpensslClient {

    private static Config mainConfig = SystemPropertiesConfig.load();

    @Test
    public void ccs() throws Exception {
        new VendorTest()
                .label("mutated_server_ccs")
                .set(new OpensslClient())
                .set(MutatedServer.from(httpsServer(), ccsConfigs(mainConfig)))
                .run();
    }
}
