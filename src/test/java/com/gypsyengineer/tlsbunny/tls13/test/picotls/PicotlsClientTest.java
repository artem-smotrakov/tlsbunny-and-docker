package com.gypsyengineer.tlsbunny.tls13.test.picotls;

import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertCheck;
import com.gypsyengineer.tlsbunny.tls13.test.picotls.client.PicotlsClient;
import org.junit.Test;

// run a client against picotls TLS 1.3 server
// make sure that the server is running on the port below
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
