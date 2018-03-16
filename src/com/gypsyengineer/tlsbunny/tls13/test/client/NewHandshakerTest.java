package com.gypsyengineer.tlsbunny.tls13.test.client;

import com.gypsyengineer.tlsbunny.tls13.connection.*;
import java.io.IOException;

public class NewHandshakerTest {

    public static void main(String[] args) throws IOException {
        new TLSConnection()
                .host("localhost")
                .port(10101)
                .send(new OutgoingClientHello())
                .expect(new IncomingServerHello())
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
