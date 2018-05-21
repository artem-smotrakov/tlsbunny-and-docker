package com.gypsyengineer.tlsbunny.tls13.test.openssl.client;

import com.gypsyengineer.tlsbunny.tls13.connection.*;
import com.gypsyengineer.tlsbunny.tls13.connection.action.composite.*;
import com.gypsyengineer.tlsbunny.tls13.test.SystemPropertiesConfig;
import com.gypsyengineer.tlsbunny.tls13.test.Config;

public class ClientAuth {

    public static void main(String[] args) throws Exception {
        Config config = SystemPropertiesConfig.load();

        Engine.init()
                .target(config.host())
                .target(config.port())
                .send(new OutgoingClientHello())
                .send(new OutgoingChangeCipherSpec())
                .require(new IncomingServerHello())
                .require(new IncomingChangeCipherSpec())
                .require(new IncomingEncryptedExtensions())
                .require(new IncomingCertificateRequest())
                .require(new IncomingCertificate())
                .require(new IncomingCertificateVerify())
                .require(new IncomingFinished())
                .send(new OutgoingCertificate()
                        .certificate(config.clientCertificate()))
                .send(new OutgoingCertificateVerify()
                        .key(config.clientKey()))
                .send(new OutgoingFinished())
                .allow(new IncomingNewSessionTicket())
                .send(new OutgoingHttpGetRequest())
                .require(new IncomingApplicationData())
                .connect()
                .run(new NoAlertCheck());
    }

}
