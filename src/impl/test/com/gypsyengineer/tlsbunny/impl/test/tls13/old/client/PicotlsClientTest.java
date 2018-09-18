package com.gypsyengineer.tlsbunny.impl.tls13.test.picotls.client;

import com.gypsyengineer.tlsbunny.impl.test.tls13.old.picotls.PicotlsClient;
import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertCheck;
import org.junit.Test;

import static junit.framework.TestCase.fail;

// run a client against picotls TLS 1.3 server
// make sure that the server is running on the port below
public class PicotlsClientTest {

    public static final int PICOTLS_PORT = 20101;

    @Test
    public void connectToPicotls() {
        try (PicotlsClient client = new PicotlsClient()) {
            client.config().port(PICOTLS_PORT);
            client.connect().engine().run(new NoAlertCheck());
        } catch (Exception e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }
}
