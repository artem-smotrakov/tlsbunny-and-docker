package com.gypsyengineer.tlsbunny.tls13.test.picotls;

import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertCheck;
import com.gypsyengineer.tlsbunny.tls13.test.picotls.client.PicotlsClient;
import org.junit.Test;

public class PicotlsClientTest {

    public static final int PICOTLS_PORT = 20101;

    @Test
    public void connectToOpenssl() throws Exception {
        try (PicotlsClient client = new PicotlsClient()) {
            client.config().port(PICOTLS_PORT);
            client.connect().run(new NoAlertCheck());
        }
    }
}
