package com.gypsyengineer.tlsbunny.tls13.test.openssl.client;

import com.gypsyengineer.tlsbunny.tls13.test.*;
import com.gypsyengineer.tlsbunny.tls13.test.common.client.CommonCertificateFuzzer;
import com.gypsyengineer.tlsbunny.utils.Output;

public class CertificateFuzzer extends CommonCertificateFuzzer {

    public CertificateFuzzer(Output output, FuzzerConfig config) {
        super(output, config, new HttpsClientAuth());
    }

    public static void main(String[] args) throws InterruptedException {
        new MultipleThreads()
                .add(config -> new CertificateFuzzer(new Output(), config), configs)
                .submit();
    }

}
