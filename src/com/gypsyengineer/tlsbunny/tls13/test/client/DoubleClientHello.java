package com.gypsyengineer.tlsbunny.tls13.test.client;

import com.gypsyengineer.tlsbunny.tls13.connection.*;

public class DoubleClientHello {

    public static void main(String[] args) throws Exception {
        CommonConfig config = new CommonConfig();

        Engine.init()
                .target(config.host())
                .target(config.port())
                .send(new OutgoingClientHello())
                .expect(new IncomingServerHello())
                .expect(new IncomingChangeCipherSpec())
                .expect(new IncomingEncryptedExtensions())
                .expect(new IncomingCertificate())
                .expect(new IncomingCertificateVerify())
                .expect(new IncomingFinished())
                .produce(new OutgoingFinished())
                .send(new OutgoingClientHello())
                .expect(new IncomingAlert())
                .connect();
    }
}
