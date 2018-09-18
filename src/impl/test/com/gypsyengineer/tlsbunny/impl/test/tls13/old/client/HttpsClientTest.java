package com.gypsyengineer.tlsbunny.impl.tls13.test.common.client;

import com.gypsyengineer.tlsbunny.tls13.client.HttpsClient;
import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertCheck;
import com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion;
import org.junit.Test;

import static junit.framework.TestCase.fail;

// run https clients against multiple TLS 1.3 servers
// make sure that the servers are running on the ports below
public class HttpsClientTest {

    public static final int OPENSSL_PORT = 10101;
    public static final int H2O_PORT = 30101;
    public static final int WOLFSSL_PORT = 40101;
    public static final int GNUTLS_PORT = 50101;
    public static final int NSS_PORT = 60101;

    @Test
    public void connectToOpenssl() {
        try (HttpsClient client = new HttpsClient()) {
            client.config().port(OPENSSL_PORT);
            client.connect().engine().run(new NoAlertCheck());
        } catch (Exception e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }

    @Test
    public void connectToH2O() {
        try (HttpsClient client = new HttpsClient()) {
            client.config().port(H2O_PORT);
            client.connect().engine().run(new NoAlertCheck());
        } catch (Exception e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }

    @Test
    public void connectToWolfssl() {
        try (HttpsClient client = new HttpsClient()) {
            client.config().port(WOLFSSL_PORT);
            client.connect().engine().run(new NoAlertCheck());
        } catch (Exception e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }

    @Test
    public void connectToGnutls() {
        try (HttpsClient client = new HttpsClient()) {
            client.config().port(GNUTLS_PORT);
            client.connect().engine().run(new NoAlertCheck());
        } catch (Exception e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }

    @Test
    public void connectToNSS() {
        try (HttpsClient client = new HttpsClient()) {
            client.config().port(NSS_PORT);
            client.version(ProtocolVersion.TLSv13_draft_28).connect()
                    .engine().run(new NoAlertCheck());
        } catch (Exception e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }
}
