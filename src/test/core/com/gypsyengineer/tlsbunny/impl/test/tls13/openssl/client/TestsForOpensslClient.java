package com.gypsyengineer.tlsbunny.impl.test.tls13.openssl.client;

import com.gypsyengineer.tlsbunny.impl.test.tls13.ImplTest;
import com.gypsyengineer.tlsbunny.impl.test.tls13.openssl.OpensslClient;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import org.junit.Test;

import static com.gypsyengineer.tlsbunny.tls13.server.HttpsServer.httpsServer;
import static com.gypsyengineer.tlsbunny.tls13.server.OneConnectionReceived.oneConnectionReceived;
import static com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup.secp256r1;

/**
 * Tests for OpenSSL s_client.
 */
public class TestsForOpensslClient {

    @Test
    public void successfulHandshake() throws Exception {
        Config serverConfig = SystemPropertiesConfig.load();

        new ImplTest()
                .set(new OpensslClient())
                .set(httpsServer()
                        .set(serverConfig)
                        .set(secp256r1)
                        .stopWhen(oneConnectionReceived()))
                .run();
    }
}
