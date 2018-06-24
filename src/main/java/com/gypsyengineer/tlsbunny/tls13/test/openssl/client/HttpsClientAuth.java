package com.gypsyengineer.tlsbunny.tls13.test.openssl.client;

import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.action.composite.*;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.tls13.test.SystemPropertiesConfig;
import com.gypsyengineer.tlsbunny.tls13.test.common.client.AbstractClient;

public class HttpsClientAuth extends AbstractClient {

    public static void main(String[] args) throws Exception {
        new HttpsClientAuth()
                .set(SystemPropertiesConfig.load())
                .set(StructFactory.getDefault())
                .connect()
                .run(new NoAlertCheck());
    }

    @Override
    public Engine connect() throws Exception {
        return Engine.init()
                .target(config.host())
                .target(config.port())
                .set(factory)
                .set(output)

                .send(new OutgoingClientHello())
                .send(new OutgoingChangeCipherSpec())
                .receive(new IncomingServerHello())
                .receive(new IncomingChangeCipherSpec())
                .receive(new IncomingEncryptedExtensions())
                .receive(new IncomingCertificateRequest())
                .receive(new IncomingCertificate())
                .receive(new IncomingCertificateVerify())
                .receive(new IncomingFinished())
                .send(new OutgoingCertificate()
                        .certificate(config.clientCertificate()))
                .send(new OutgoingCertificateVerify()
                        .key(config.clientKey()))
                .send(new OutgoingFinished())
                .allow(new IncomingNewSessionTicket())
                .send(new OutgoingHttpGetRequest())
                .receive(new IncomingApplicationData())
                .connect();
    }

}
