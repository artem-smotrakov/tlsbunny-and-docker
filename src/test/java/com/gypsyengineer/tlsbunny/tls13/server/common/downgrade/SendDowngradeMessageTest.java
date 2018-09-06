package com.gypsyengineer.tlsbunny.tls13.server.common.downgrade;

import com.gypsyengineer.tlsbunny.tls13.client.common.Client;
import com.gypsyengineer.tlsbunny.tls13.client.common.SendAlertAfterHello;
import com.gypsyengineer.tlsbunny.tls13.connection.NoExceptionCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.action.DowngradeMessageCheck;
import com.gypsyengineer.tlsbunny.tls13.server.common.OneConnectionReceived;
import com.gypsyengineer.tlsbunny.tls13.server.common.Server;
import com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.Output;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import org.junit.Test;

import static com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion.TLSv10;
import static com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion.TLSv11;
import static com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion.TLSv12;
import static org.junit.Assert.assertFalse;

public class SendDowngradeMessageTest {

    // TODO: how can we avoid duplicating these strings?
    private static final String serverCertificatePath = "certs/server_cert.der";
    private static final String serverKeyPath = "certs/server_key.pkcs8";

    @Test
    public void tls12() throws Exception {
        test(TLSv12);
    }

    @Test
    public void tls11() throws Exception {
        test(TLSv11);
    }

    @Test
    public void tls10() throws Exception {
        test(TLSv10);
    }

    private static void test(ProtocolVersion version) throws Exception {
        Output serverOutput = new Output("server");
        Output clientOutput = new Output("client");

        Config config = SystemPropertiesConfig.load();
        config.serverCertificate(serverCertificatePath);
        config.serverKey(serverKeyPath);

        Server server = SendDowngradeMessage.server(serverOutput, config, version)
                .stopWhen(new OneConnectionReceived());

        Config clientConfig = SystemPropertiesConfig.load();

        Client client = new SendAlertAfterHello()
                .set(clientOutput);

        try (client; server; clientOutput; serverOutput) {
            server.start();

            clientConfig.port(server.port());
            client.set(clientConfig);

            client.connect()
                    .run(new NoExceptionCheck())
                    .run(new DowngradeMessageCheck().set(version));

            server.await();
        }

        assertFalse(server.failed());
    }


}
