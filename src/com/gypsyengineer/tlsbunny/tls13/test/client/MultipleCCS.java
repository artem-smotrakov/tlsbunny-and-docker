package com.gypsyengineer.tlsbunny.tls13.test.client;

import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.NoAlertCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.action.composite.*;

public class MultipleCCS {

    public static void main(String[] args) throws Exception {
        CommonConfig config = new CommonConfig();

        Engine.init()
                .target(config.host())
                .target(config.port())
                .send(new OutgoingClientHello())
                .send(new OutgoingChangeCipherSpec())
                .send(new OutgoingChangeCipherSpec())
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
    }

}
