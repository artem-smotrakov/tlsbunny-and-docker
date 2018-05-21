package com.gypsyengineer.tlsbunny.tls13.test.openssl.client;

import com.gypsyengineer.tlsbunny.tls13.connection.*;
import com.gypsyengineer.tlsbunny.tls13.connection.action.composite.*;
import com.gypsyengineer.tlsbunny.tls13.test.SystemPropertiesConfig;

public class DoubleClientHello {

    public static void main(String[] args) throws Exception {
        SystemPropertiesConfig config = SystemPropertiesConfig.load();

        Engine.init()
                .target(config.host())
                .target(config.port())
                .send(new OutgoingClientHello())
                .require(new IncomingServerHello())
                .require(new IncomingChangeCipherSpec())
                .require(new IncomingEncryptedExtensions())
                .require(new IncomingCertificate())
                .require(new IncomingCertificateVerify())
                .require(new IncomingFinished())
                .run(new OutgoingFinished())
                .send(new OutgoingClientHello())
                .require(new IncomingAlert())
                .connect();
    }
}
