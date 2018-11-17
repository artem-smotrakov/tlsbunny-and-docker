package com.gypsyengineer.tlsbunny.impl.test.tls13.openssl.client;

import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import org.junit.Test;

import static com.gypsyengineer.tlsbunny.tls13.server.HttpsServer.httpsServer;

public class FuzzingForOpensslClient {

    private static Config serverConfig = SystemPropertiesConfig.load();

    @Test
    // TODO check for ASan messages
    public void ccs() throws Exception {
        //new ImplTest()
        //        .set(new OpensslClient())
        //        .set(mutatedServer(httpsServer().set(secp256r1))
        //                .stopWhen(oneConnectionReceived()))
        //        .run();
    }
}
