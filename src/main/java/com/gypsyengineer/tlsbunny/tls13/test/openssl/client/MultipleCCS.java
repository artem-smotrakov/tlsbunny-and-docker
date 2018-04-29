package com.gypsyengineer.tlsbunny.tls13.test.openssl.client;

import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.action.composite.*;
import com.gypsyengineer.tlsbunny.tls13.test.CommonConfig;
import com.gypsyengineer.tlsbunny.utils.Output;

public class MultipleCCS {

    public static int N = 100;

    public static void main(String[] args) throws Exception {
        CommonConfig config = CommonConfig.load();
        Output output = new Output();

        output.info("test 0: send a CCS message after each handshake message");
        try {
            Engine.init()
                    .target(config.host())
                    .target(config.port())
                    .set(output)
                    .send(new OutgoingClientHello())
                    .send(new OutgoingChangeCipherSpec())
                    .require(new IncomingServerHello())
                    .send(new OutgoingChangeCipherSpec())
                    .require(new IncomingEncryptedExtensions())
                    .send(new OutgoingChangeCipherSpec())
                    .require(new IncomingCertificate())
                    .send(new OutgoingChangeCipherSpec())
                    .require(new IncomingCertificateVerify())
                    .send(new OutgoingChangeCipherSpec())
                    .require(new IncomingFinished())
                    .send(new OutgoingChangeCipherSpec())
                    .send(new OutgoingFinished())
                    .allow(new IncomingNewSessionTicket())
                    .send(new OutgoingHttpGetRequest())
                    .require(new IncomingApplicationData())
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
                        .require(new IncomingServerHello())
                        .require(new IncomingChangeCipherSpec())
                        .require(new IncomingEncryptedExtensions())
                        .require(new IncomingCertificate())
                        .require(new IncomingCertificateVerify())
                        .require(new IncomingFinished())
                        .send(new OutgoingFinished())
                        .allow(new IncomingNewSessionTicket())
                        .send(new OutgoingHttpGetRequest())
                        .require(new IncomingApplicationData())
                        .connect()
                        .run(new NoAlertCheck());

                output.info("test passed");
            } finally {
                output.flush();
            }
        }
    }

}
