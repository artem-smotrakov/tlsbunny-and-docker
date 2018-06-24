package com.gypsyengineer.tlsbunny.tls13.test.common.client;

import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertCheck;
import com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion;
import org.junit.Test;

// run https clients against multiple TLS 1.3 servers
// make sure that the servers are running on the ports below
public class HttpsClientTest {

    public static final int OPENSSL_PORT = 10101;
    public static final int H2O_PORT = 30101;
    public static final int WOLFSSL_PORT = 40101;
    public static final int GNUTLS_PORT = 50101;
    public static final int NSS_PORT = 60101;

    @Test
    public void connectToOpenssl() throws Exception {
        try (HttpsClient client = new HttpsClient()) {
            client.config().port(OPENSSL_PORT);
            client.connect().run(new NoAlertCheck());
        }
    }

    @Test
    public void connectToH2O() throws Exception {
        try (HttpsClient client = new HttpsClient()) {
            client.config().port(H2O_PORT);
            client.connect().run(new NoAlertCheck());
        }
    }

    @Test
    public void connectToWolfssl() throws Exception {
        try (HttpsClient client = new HttpsClient()) {
            client.config().port(WOLFSSL_PORT);
            client.connect().run(new NoAlertCheck());
        }
    }

    @Test
    public void connectToGnutls() throws Exception {
        try (HttpsClient client = new HttpsClient()) {
            client.config().port(GNUTLS_PORT);
            client.connect().run(new NoAlertCheck());
        }
    }

    @Test
    public void connectToNSS() throws Exception {
        try (HttpsClient client = new HttpsClient()) {
            client.config().port(NSS_PORT);
            client.version(ProtocolVersion.TLSv13_draft_28).connect()
                    .run(new NoAlertCheck());
        }
    }
}
