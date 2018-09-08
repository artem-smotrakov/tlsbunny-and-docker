package com.gypsyengineer.tlsbunny;

import org.junit.Test;

import javax.net.ssl.SSLContext;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * This is a fake test which has to be run first because it initialized JSSE settings.
 * Once JSSE is loaded, then its settings (like paths ot keystores, and passwords)
 * can't be changed. Make sure that this fake test runs before other tests,
 * otherwise JSSE-based tests may fail.
 */
public class BaseTest {

    public static final long delay = 1000; // in millis

    public static final String keystore = "certs/keystore.p12";
    public static final String serverCertificatePath = "certs/server_cert.der";
    public static final String serverKeyPath = "certs/server_key.pkcs8";

    private static final String keystorePassword = "changeme";
    private static final String debug = ""; // 'all' prints everything

    static {
        System.setProperty("javax.net.ssl.keyStore", keystore);
        System.setProperty("javax.net.ssl.keyStorePassword", keystorePassword);
        System.setProperty("javax.net.ssl.trustStore", keystore);
        System.setProperty("javax.net.ssl.trustStorePassword", keystorePassword);

        if (debug != null && !debug.isEmpty()) {
            System.setProperty("javax.net.debug", debug);
        }
    }

    public static boolean supportsTls13() {
        try {
            return List.of(SSLContext.getDefault().getSupportedSSLParameters().getProtocols())
                    .contains("TLSv1.3");
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
    }

    // this a fake test to make JUnit happy
    @Test
    public void init() {
        assertTrue(true);
    }
}
