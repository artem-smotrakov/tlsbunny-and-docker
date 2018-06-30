package com.gypsyengineer.tlsbunny.tls13.test.wolfssl.client;

import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertCheck;
import org.junit.Test;

import static junit.framework.TestCase.fail;

public class WolfsslHttpsClientTest {

    public static final int WOLFSSL_PORT = 40101;

    @Test
    public void connectToWolfssl() {
        try (WolfsslHttpsClient client = new WolfsslHttpsClient()) {
            client.config().port(WOLFSSL_PORT);
            client.connect().run(new NoAlertCheck());
        } catch (Exception e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }
}