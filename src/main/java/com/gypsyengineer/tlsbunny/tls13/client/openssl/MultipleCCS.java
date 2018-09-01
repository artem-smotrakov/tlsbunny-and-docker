package com.gypsyengineer.tlsbunny.tls13.client.openssl;

import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.action.composite.*;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import com.gypsyengineer.tlsbunny.utils.Output;

public class MultipleCCS {

    public static int N = 100;

    public static void main(String[] args) throws Exception {
        SystemPropertiesConfig config = SystemPropertiesConfig.load();
        Output output = new Output();

        output.info("test 0: send a CCS message after each handshake message");
        try {
            Engine.init()
                    .target(config.host())
                    .target(config.port())
                    .set(output)
                    .send(new OutgoingClientHello())
                    .send(new OutgoingChangeCipherSpec())
                    .receive(new IncomingServerHello())
                    .send(new OutgoingChangeCipherSpec())
                    .receive(new IncomingEncryptedExtensions())
                    .send(new OutgoingChangeCipherSpec())
                    .receive(new IncomingCertificate())
                    .send(new OutgoingChangeCipherSpec())
                    .receive(new IncomingCertificateVerify())
                    .send(new OutgoingChangeCipherSpec())
                    .receive(new IncomingFinished())
                    .send(new OutgoingChangeCipherSpec())
                    .send(new OutgoingFinished())
                    .send(new OutgoingHttpGetRequest())
                    .receive(new IncomingApplicationData())
                    .connect()
                    .run(new NoAlertCheck());

            output.info("test passed");
        } finally {
            output.flush();
        }

        for (int i = 1; i < N; i++) {
            output.info("test %d: send %d CCS messages", i, i);
            try {
                Engine.init()
                        .target(config.host())
                        .target(config.port())
                        .set(output)
                        .send(new OutgoingClientHello())
                        .send(i, () -> new OutgoingChangeCipherSpec())
                        .receive(new IncomingServerHello())
                        .receive(new IncomingChangeCipherSpec())
                        .receive(new IncomingEncryptedExtensions())
                        .receive(new IncomingCertificate())
                        .receive(new IncomingCertificateVerify())
                        .receive(new IncomingFinished())
                        .send(new OutgoingFinished())
                        .send(new OutgoingHttpGetRequest())
                        .receive(new IncomingApplicationData())
                        .connect()
                        .run(new NoAlertCheck());

                output.info("test passed");
            } finally {
                output.flush();
            }
        }
    }

}
