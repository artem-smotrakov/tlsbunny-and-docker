package com.gypsyengineer.tlsbunny.tls13.test.client;

import com.gypsyengineer.tlsbunny.tls13.connection.*;

public class NewHandshakerTest {

    public static void main(String[] args) throws Exception {
        TLSConnection.create()
                .host("localhost")
                .port(10101)
                .send(new OutgoingClientHello())
                .expect(new IncomingServerHello())
                .expect(new IncomingChangeCipherSpec())
                .expect(new IncomingEncryptedExtensions())
                .expect(new IncomingCertificate())
                .expect(new IncomingCertificateVerify())
                .send(new OutgoingFinished())
                .expect(new IncomingFinished())
                .allow(new IncomingNewSessionTicket())
                .send(new OutgoingApplicationData())
                .expect(new IncomingApplicationData())
                .run()
                .check(new Success());
    }

}
