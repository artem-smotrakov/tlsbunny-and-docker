package com.gypsyengineer.tlsbunny.tls13.test.openssl.client;

import com.gypsyengineer.tlsbunny.tls13.connection.*;
import com.gypsyengineer.tlsbunny.tls13.connection.action.composite.*;
import com.gypsyengineer.tlsbunny.tls13.fuzzer.InvalidChangeCipherSpec;
import com.gypsyengineer.tlsbunny.tls13.test.CommonConfig;
import com.gypsyengineer.tlsbunny.tls13.test.Config;
import com.gypsyengineer.tlsbunny.utils.Output;

public class InvalidCCS {

    public static void main(String[] args) throws Exception {
        Config config = new CommonConfig();
        Output output = new Output();
        InvalidChangeCipherSpec invalidChangeCipherSpec = new InvalidChangeCipherSpec();
        Analyzer analyzer = new NoAlertAnalyzer().set(output);

        while (invalidChangeCipherSpec.canFuzz()) {
            Output testOutput = new Output();
            testOutput.info("test: %s", invalidChangeCipherSpec.getState());
            Engine.init()
                    .label(invalidChangeCipherSpec.getState())
                    .target(config.host())
                    .target(config.port())
                    .set(testOutput)
                    .send(new OutgoingClientHello())
                    .send(invalidChangeCipherSpec)
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
                    .connect()
                    .apply(analyzer);

            invalidChangeCipherSpec.moveOn();
        }

        analyzer.run();

        output.info("done!");
        output.flush();
    }

}
