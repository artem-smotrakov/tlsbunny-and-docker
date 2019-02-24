package com.gypsyengineer.tlsbunny.vendor.test.tls13.common.server;

import com.gypsyengineer.tlsbunny.tls13.server.Server;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.Utils;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.VendorTest;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.gypsyengineer.tlsbunny.tls13.client.HttpsClientAuth.httpsClientAuth;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.MultiConfigClient.multiConfigClient;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.MutatedClient.mutatedClient;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.MutatedConfigs.certificateConfigs;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.MutatedConfigs.certificateVerifyConfigs;
import static com.gypsyengineer.tlsbunny.vendor.test.tls13.Utils.checkForASanFindings;

public abstract class FuzzingForServerWithClientAuth {

    protected static Server server;
    protected static Config mainConfig = SystemPropertiesConfig.load();

    // provide setUp() method

    @Before
    public void serverReady() throws IOException, InterruptedException {
        Utils.waitServerReady(server);
    }

    @Test
    public void certificate() throws Exception {
        new VendorTest()
                .set(multiConfigClient()
                        .of(mutatedClient().of(httpsClientAuth()))
                        .configs(certificateConfigs(mainConfig)))
                .set(server)
                .run();
    }

    @Test
    public void certificateVerify() throws Exception {
        new VendorTest()
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
