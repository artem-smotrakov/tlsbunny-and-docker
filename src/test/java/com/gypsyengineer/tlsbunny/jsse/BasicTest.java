package com.gypsyengineer.tlsbunny.jsse;

import com.gypsyengineer.tlsbunny.tls13.client.common.Client;
import com.gypsyengineer.tlsbunny.tls13.client.common.HttpsClient;
import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertAnalyzer;
import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.NoExceptionCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.SuccessCheck;
import com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.Output;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import org.junit.Test;

public class BasicTest {

    private static final long delay = 1000; // in millis
    private static final String serverCertificatePath = "certs/server_cert.der";
    private static final String serverKeyPath = "certs/server_key.pkcs8";

    @Test
    public void httpsClient() throws Exception {
        Config serverConfig = SystemPropertiesConfig.load();
        serverConfig.serverCertificate(serverCertificatePath);
        serverConfig.serverKey(serverKeyPath);

        Output serverOutput = new Output("server");
        Output clientOutput = new Output("client");

        Client client = new HttpsClient()
                .version(ProtocolVersion.TLSv13)
                .set(StructFactory.getDefault())
                .set(clientOutput);

        SimpleEchoServer server = SimpleEchoServer.create()
                .set(serverConfig)
                .set(serverOutput);

        try (client; server; clientOutput; serverOutput) {
            new Thread(server).start();
            Thread.sleep(delay);

            Config clientConfig = SystemPropertiesConfig.load();
            clientConfig.port(server.port());

            client.set(clientConfig).set(clientOutput);

            client.connect()
                    .run(new NoAlertCheck())
                    .run(new SuccessCheck())
                    .run(new NoExceptionCheck())
                    .apply(new NoAlertAnalyzer());
        }
    }
}
