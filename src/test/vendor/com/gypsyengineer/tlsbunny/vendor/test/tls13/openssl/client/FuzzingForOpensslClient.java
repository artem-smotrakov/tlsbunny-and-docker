package com.gypsyengineer.tlsbunny.vendor.test.tls13.openssl.client;

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
                .set(MutatedServer.from(httpsServer())
                        .set(ccsConfigs(mainConfig)))
                .run();
    }

    @Test
    public void tlsPlaintext() throws Exception {
        new VendorTest()
                .label("mutated_server_tls_plaintext")
                .set(new OpensslClient())
                .set(MutatedServer.from(httpsServer())
                        .set(tlsPlaintextConfigs(mainConfig)))
                .run();
    }

    @Test
    public void handshake() throws Exception {
        new VendorTest()
                .label("mutated_server_handshake")
                .set(new OpensslClient())
                .set(MutatedServer.from(httpsServer())
                        .set(handshakeConfigs(mainConfig)))
                .run();
    }

    @Test
    public void serverHello() throws Exception {
        new VendorTest()
                .label("mutated_server_server_hello")
                .set(new OpensslClient())
                .set(MutatedServer.from(httpsServer())
                        .set(serverHelloConfigs(mainConfig)))
                .run();
    }

    @Test
    public void certificate() throws Exception {
        new VendorTest()
                .label("mutated_server_certificate")
                .set(new OpensslClient())
                .set(MutatedServer.from(httpsServer())
                        .set(certificateConfigs(mainConfig)))
                .run();
    }

    @Test
    public void certificateVerify() throws Exception {
        new VendorTest()
                .label("mutated_server_certificate_verify")
                .set(new OpensslClient())
                .set(MutatedServer.from(httpsServer())
                        .set(certificateVerifyConfigs(mainConfig)))
                .run();
    }

    @Test
    public void finished() throws Exception {
        new VendorTest()
                .label("mutated_server_finished")
                .set(new OpensslClient())
                .set(MutatedServer.from(httpsServer())
                        .set(finishedConfigs(mainConfig)))
                .run();
    }

    @Test
    public void extensionVector() throws Exception {
        new VendorTest()
                .label("mutated_server_extension_vector")
                .set(new OpensslClient())
                .set(MutatedServer.from(httpsServer())
                        .set(extensionVectorConfigs(mainConfig)))
                .run();
    }

    // TODO: implement deep handshake fuzzing
    // TODO: fuzz CertificateRequest
}
