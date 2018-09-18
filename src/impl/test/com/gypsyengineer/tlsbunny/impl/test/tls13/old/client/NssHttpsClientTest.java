package com.gypsyengineer.tlsbunny.impl.tls13.test.nss.client;

import com.gypsyengineer.tlsbunny.impl.test.tls13.old.nss.NssHttpsClient;
import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertCheck;
import org.junit.Test;

import static junit.framework.TestCase.fail;

public class NssHttpsClientTest {

    public static final int NSS_PORT = 60101;

    @Test
    public void connectToNss() {
        try (NssHttpsClient client = new NssHttpsClient()) {
            client.config().port(NSS_PORT);
            client.connect().engine().run(new NoAlertCheck());
        } catch (Exception e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }
}
