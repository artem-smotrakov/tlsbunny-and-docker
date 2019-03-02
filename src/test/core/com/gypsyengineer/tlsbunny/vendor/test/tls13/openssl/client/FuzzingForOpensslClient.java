package com.gypsyengineer.tlsbunny.vendor.test.tls13.openssl.client;

import com.gypsyengineer.tlsbunny.tls13.fuzzer.DeepHandshakeFuzzerConfigs;
import com.gypsyengineer.tlsbunny.tls13.fuzzer.MutatedServer;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.VendorTest;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.openssl.OpensslClient;
import org.junit.Test;

import static com.gypsyengineer.tlsbunny.tls13.fuzzer.MutatedConfigs.*;
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

    @Test
    public void tlsPlaintext() throws Exception {
        new VendorTest()
                .label("mutated_server_tls_plaintext")
                .set(new OpensslClient())
                .set(MutatedServer.from(httpsServer(), tlsPlaintextConfigs(mainConfig)))
                .run();
    }

    @Test
    public void handshake() throws Exception {
        new VendorTest()
                .label("mutated_server_handshake")
                .set(new OpensslClient())
                .set(MutatedServer.from(httpsServer(), handshakeConfigs(mainConfig)))
                .run();
    }

    @Test
    public void serverHello() throws Exception {
        new VendorTest()
                .label("mutated_server_server_hello")
                .set(new OpensslClient())
                .set(MutatedServer.from(httpsServer(), serverHelloConfigs(mainConfig)))
                .run();
    }

    @Test
    public void finished() throws Exception {
        new VendorTest()
                .label("mutated_server_finished")
                .set(new OpensslClient())
                .set(MutatedServer.from(httpsServer(), finishedConfigs(mainConfig)))
                .run();
    }

    @Test
    public void extensionVector() throws Exception {
        new VendorTest()
                .label("mutated_server_extension_vector")
                .set(new OpensslClient())
                .set(MutatedServer.from(httpsServer(), extensionVectorConfigs(mainConfig)))
                .run();
    }

    @Test
    public void deepHandshakeFuzzer() throws Exception {
        new VendorTest()
                .label("mutated_server_deep_handshake_fuzzer")
                .set(new OpensslClient())
                .set(MutatedServer.from(httpsServer(), DeepHandshakeFuzzerConfigs.noClientAuth(mainConfig)))
                .run();
    }
}
