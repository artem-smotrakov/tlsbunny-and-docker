package com.gypsyengineer.tlsbunny.vendor.test.tls13.old.jsse;

import com.gypsyengineer.tlsbunny.tls13.client.Client;
import com.gypsyengineer.tlsbunny.tls13.client.HttpsClient;
import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertAnalyzer;
import com.gypsyengineer.tlsbunny.tls13.connection.check.NoAlertCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.check.NoExceptionCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.check.SuccessCheck;
import com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.Output;
import com.gypsyengineer.tlsbunny.utils.SimpleOutput;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import org.junit.Test;

public class HandshakeTest extends BaseTest {

    @Test
    public void basic() throws Exception {
        if (!supportsTls13()) {
            System.err.println("Warning: TLS 1.3 is not supported by JSSE, skip the test");
            return;
        }

        Config serverConfig = SystemPropertiesConfig.load();
        serverConfig.serverCertificate(serverCertificatePath);
        serverConfig.serverKey(serverKeyPath);

        Output serverOutput = new SimpleOutput("server");
        SimpleOutput clientOutput = new SimpleOutput("client");

        Client client = new HttpsClient()
                .version(ProtocolVersion.TLSv13)
                .set(clientOutput);

        SimpleHttpsServer server = SimpleHttpsServer.create()
                .set(serverConfig)
                .set(serverOutput);

        try (client; server; clientOutput; serverOutput) {
            new Thread(server).start();
            Thread.sleep(delay);

            Config clientConfig = SystemPropertiesConfig.load().port(server.port());
            client.set(clientConfig).set(clientOutput);

            Engine[] engines = client.connect().engines();

            for (Engine engine : engines) {
                engine.run(new NoAlertCheck())
                        .run(new SuccessCheck())
                        .run(new NoExceptionCheck())
                        .apply(new NoAlertAnalyzer());
            }

            server.stop();
            //server.await();
        }
    }

    // standalone interface
    public static void main(String[] args) throws Exception {
        HandshakeTest test = new HandshakeTest();
        test.basic();
    }
}
