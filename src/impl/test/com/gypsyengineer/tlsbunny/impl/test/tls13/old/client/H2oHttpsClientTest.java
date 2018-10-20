package com.gypsyengineer.tlsbunny.impl.tls13.test.h2o.client;

import com.gypsyengineer.tlsbunny.impl.test.tls13.old.h2o.H2oHttpsClient;
import com.gypsyengineer.tlsbunny.tls13.connection.check.NoAlertCheck;
import org.junit.Test;

import static junit.framework.TestCase.fail;

public class H2oHttpsClientTest {

    public static final int H2O_PORT = 30101;

    @Test
    public void connectToH2O() {
        try (H2oHttpsClient client = new H2oHttpsClient()) {
            client.config().port(H2O_PORT);
            client.connect().engine().run(new NoAlertCheck());
        } catch (Exception e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }
}
