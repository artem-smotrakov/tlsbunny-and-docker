package com.gypsyengineer.tlsbunny.tls13.test.client;

import com.gypsyengineer.tlsbunny.tls13.action.*;
import com.gypsyengineer.tlsbunny.tls13.analysis.CheckHttpContent;
import com.gypsyengineer.tlsbunny.tls13.connection.TLSConnection;

public class NewHandshakerTest {

    public static void main(String[] args) {
        new TLSConnection()
                .required(new SendClientHello())
                .required(new ReceiveServerHello())
                .required(new ReceiveCertificate())
                .required(new ReceiveCertificateVerify())
                .required(new SendFinished())
                .required(new ReceiveFinished())
                .optional(new ReceiveNewSessionTicket())
                .required(new SendApplicationData())
                .required(new ReceiveApplicationData())
                .run()
                .analyze(new CheckHttpContent());
    }

}
