package com.gypsyengineer.tlsbunny.impl.test.tls13.openssl.server;

import com.gypsyengineer.tlsbunny.impl.test.tls13.ImplTest;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import org.junit.Test;

import static com.gypsyengineer.tlsbunny.tls13.server.HttpsServer.httpsServer;
import static com.gypsyengineer.tlsbunny.tls13.server.OneConnectionReceived.oneConnectionReceived;
import static com.gypsyengineer.tlsbunny.tls13.server.fuzzer.MutatedServer.mutatedServer;
import static com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup.secp256r1;

public class MutatedFuzzingForOpenSSLClient {

    private static Config serverConfig = SystemPropertiesConfig.load();

    @Test
    // TODO check for ASan messages
    public void ccs() throws Exception {
        new ImplTest()
                .set(new OpensslClient())
                .set(mutatedServer(httpsServer().set(secp256r1))
                        .stopWhen(oneConnectionReceived()))
                .run();
    }
}
