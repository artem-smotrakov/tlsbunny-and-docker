package com.gypsyengineer.tlsbunny.tls13.test.openssl.client;

import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.action.composite.*;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.tls13.test.SystemPropertiesConfig;
import com.gypsyengineer.tlsbunny.tls13.test.Config;
import com.gypsyengineer.tlsbunny.tls13.test.common.client.Client;

public class HttpsClientAuth implements Client {

    public static void main(String[] args) throws Exception {
        new HttpsClientAuth()
                .connect(SystemPropertiesConfig.load(), StructFactory.getDefault())
                .run(new NoAlertCheck());
    }

    @Override
    public Engine connect(Config config, StructFactory factory) throws Exception {
        return Engine.init()
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
                .connect();
    }

}
