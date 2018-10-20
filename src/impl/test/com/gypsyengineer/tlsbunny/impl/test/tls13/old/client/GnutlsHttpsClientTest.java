package com.gypsyengineer.tlsbunny.impl.tls13.test.gnutls.client;

import com.gypsyengineer.tlsbunny.impl.test.tls13.old.gnutls.GnutlsHttpsClient;
import com.gypsyengineer.tlsbunny.tls13.connection.check.NoAlertCheck;
import org.junit.Test;

import static junit.framework.TestCase.fail;

public class GnutlsHttpsClientTest {

    public static final int GNUTLS_PORT = 50101;

    @Test
    public void connectToGnutls() {
        try (GnutlsHttpsClient client = new GnutlsHttpsClient()) {
            client.config().port(GNUTLS_PORT);
            client.connect().engine().run(new NoAlertCheck());
        } catch (Exception e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }
}
