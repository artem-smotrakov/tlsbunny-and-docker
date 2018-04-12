package com.gypsyengineer.tlsbunny.tls13.test.client;

import com.gypsyengineer.tlsbunny.tls13.connection.*;

public class CCSAfterHandshake {

    public static void main(String[] args) throws Exception {
        CommonConfig config = new CommonConfig();

        // TODO: fix it
        Engine.init()
                .target(config.host())
                .target(config.port())
                //.expect(new EncryptedHandshakeAlert())
                //.expect(new EncryptedApplicationDataAlert())
                .send(new OutgoingClientHello())
                .send(new OutgoingChangeCipherSpec())
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
                .send(new OutgoingChangeCipherSpec())
                .require(new AnythingIncoming())
                .connect()
                .run(new AlertCheck());
    }

}
