package com.gypsyengineer.tlsbunny.vendor.test.tls13.common.server;

import com.gypsyengineer.tlsbunny.tls13.fuzzer.DeepHandshakeFuzzerConfigs;
import com.gypsyengineer.tlsbunny.tls13.server.Server;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.Utils;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.VendorTest;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.gypsyengineer.tlsbunny.tls13.client.HttpsClient.httpsClient;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.DeepHandshakeFuzzyClient.deepHandshakeFuzzyClient;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.MultiConfigClient.multiConfigClient;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.MutatedClient.mutatedClient;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.MutatedConfigs.*;
import static com.gypsyengineer.tlsbunny.vendor.test.tls13.Utils.checkForASanFindings;

public abstract class FuzzingForServer {

    protected static Server server;
    protected static Config mainConfig = SystemPropertiesConfig.load();

    // provide setUp() method!

    @Before
    public void serverReady() throws IOException, InterruptedException {
        Utils.waitServerReady(server);
    }

    @Test
    public void ccs() throws Exception {
        new VendorTest()
                .label("mutated_client_ccs")
                .set(multiConfigClient()
                        .from(mutatedClient().from(httpsClient()))
                        .configs(ccsConfigs(mainConfig)))
                .set(server)
                .run();
    }

    @Test
    public void tlsPlaintext() throws Exception {
        new VendorTest()
                .label("mutated_client_tls_plaintext")
                .set(multiConfigClient()
                        .from(mutatedClient().from(httpsClient()))
                        .configs(tlsPlaintextConfigs(mainConfig)))
                .set(server)
                .run();
    }

    @Test
    public void handshake() throws Exception {
        new VendorTest()
                .label("mutated_client_handshake")
                .set(multiConfigClient()
                        .from(mutatedClient().from(httpsClient()))
                        .configs(handshakeConfigs(mainConfig)))
                .set(server)
                .run();
    }

    @Test
    public void clientHello() throws Exception {
        new VendorTest()
                .label("mutated_client_client_hello")
                .set(multiConfigClient()
                        .from(mutatedClient().from(httpsClient()))
                        .configs(clientHelloConfigs(mainConfig)))
                .set(server)
                .run();
    }

    @Test
    public void finished() throws Exception {
        new VendorTest()
                .label("mutated_client_finished")
                .set(multiConfigClient()
                        .from(mutatedClient().from(httpsClient()))
                        .configs(finishedConfigs(mainConfig)))
                .set(server)
                .run();
    }

    @Test
    public void cipherSuites() throws Exception {
        new VendorTest()
                .label("mutated_client_cipher_suites")
                .set(multiConfigClient()
                        .from(mutatedClient().from(httpsClient()))
                        .configs(cipherSuitesConfigs(mainConfig)))
                .set(server)
                .run();
    }

    @Test
    public void extensionVector() throws Exception {
        new VendorTest()
                .label("mutated_client_extension_vector")
                .set(multiConfigClient()
                        .from(mutatedClient().from(httpsClient()))
                        .configs(extensionVectorConfigs(mainConfig)))
                .set(server)
                .run();
    }

    @Test
    public void legacyCompressionMethods() throws Exception {
        new VendorTest()
                .label("mutated_client_legacy_compression_methods")
                .set(multiConfigClient()
                        .from(mutatedClient().from(httpsClient()))
                        .configs(legacyCompressionMethodsConfigs(mainConfig)))
                .set(server)
                .run();
    }

    @Test
    public void legacySessionId() throws Exception {
        new VendorTest()
                .label("mutated_client_legacy_session_id")
                .set(multiConfigClient()
                        .from(mutatedClient().from(httpsClient()))
                        .configs(legacySessionIdConfigs(mainConfig)))
                .set(server)
                .run();
    }

    @Test
    public void deepHandshakeFuzzer() throws Exception {
        new VendorTest()
                .label("mutated_client_deep_handshake_fuzzer")
                .set(multiConfigClient()
                        .from(deepHandshakeFuzzyClient().of(httpsClient()))
                        .configs(DeepHandshakeFuzzerConfigs.noClientAuth(mainConfig)))
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
