package com.gypsyengineer.tlsbunny.tls13.test.openssl.client;

import com.gypsyengineer.tlsbunny.tls13.connection.*;
import com.gypsyengineer.tlsbunny.tls13.connection.action.composite.*;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.tls13.test.SystemPropertiesConfig;
import com.gypsyengineer.tlsbunny.tls13.test.common.client.AbstractClient;
import com.gypsyengineer.tlsbunny.utils.Output;

public class AnotherHttpsClient extends AbstractClient {

    public static void main(String[] args) throws Exception {
        try (Output output = new Output()) {
            new AnotherHttpsClient()
                    .set(SystemPropertiesConfig.load())
                    .set(StructFactory.getDefault())
                    .set(output)
                    .connect()
                    .run(new NoAlertCheck());
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
                .receive(new IncomingCertificate())
                .receive(new IncomingCertificateVerify())
                .receive(new IncomingFinished())
                .send(new OutgoingFinished())
                .send(new OutgoingHttpGetRequest())
                .receive(new IncomingApplicationData());
    }

}
