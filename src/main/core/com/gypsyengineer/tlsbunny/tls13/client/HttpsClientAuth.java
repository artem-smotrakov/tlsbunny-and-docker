package com.gypsyengineer.tlsbunny.tls13.client;

import com.gypsyengineer.tlsbunny.tls13.connection.Check;
import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.action.composite.*;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.utils.Output;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;

import java.util.List;

public class HttpsClientAuth extends SingleConnectionClient {

    public static void main(String[] args) throws Exception {
        try (Output output = new Output()) {
            new HttpsClientAuth()
                    .set(output)
                    .set(SystemPropertiesConfig.load())
                    .set(StructFactory.getDefault())
                    .connect();
        }
    }

    @Override
    protected Engine createEngine() throws Exception {
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
                .send(new OutgoingClientCertificate()
                        .certificate(config.clientCertificate()))
                .send(new OutgoingClientCertificateVerify()
                        .key(config.clientKey()))
                .send(new OutgoingFinished())
                .send(new OutgoingHttpGetRequest())
                .receive(new IncomingApplicationData());
    }

    @Override
    protected List<Check> createChecks() {
        return List.of(new NoAlertCheck());
    }

}
