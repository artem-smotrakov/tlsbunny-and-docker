package com.gypsyengineer.tlsbunny.tls13.test.openssl.client;

import com.gypsyengineer.tlsbunny.tls13.client.openssl.OpensslHttpsClient;
import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertCheck;
import org.junit.Test;

import static junit.framework.TestCase.fail;

public class OpensslHttpsClientTest {

    public static final int OPENSSL_PORT = 10101;

    @Test
    public void connectToOpenssl() {
        try (OpensslHttpsClient client = new OpensslHttpsClient()) {
            client.config().port(OPENSSL_PORT);
            client.connect().run(new NoAlertCheck());
        } catch (Exception e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }
}
