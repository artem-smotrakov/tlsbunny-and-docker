package com.gypsyengineer.tlsbunny.tls13.test.openssl.client;

import com.gypsyengineer.tlsbunny.tls13.test.*;
import com.gypsyengineer.tlsbunny.tls13.test.common.client.CommonCertificateVerifyFuzzer;
import com.gypsyengineer.tlsbunny.tls13.test.common.client.MultipleThreads;
import com.gypsyengineer.tlsbunny.utils.Output;

public class CertificateVerifyFuzzer extends CommonCertificateVerifyFuzzer {

    public CertificateVerifyFuzzer(Output output, FuzzerConfig config) {
        super(output, config, new HttpsClientAuth());
    }

    public static void main(String[] args) throws InterruptedException {
        new MultipleThreads()
                .add(config -> new CertificateVerifyFuzzer(new Output(), config), configs)
                .submit();
    }

}
