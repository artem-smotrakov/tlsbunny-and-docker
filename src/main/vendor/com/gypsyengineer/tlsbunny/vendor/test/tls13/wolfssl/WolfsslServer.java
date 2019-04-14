package com.gypsyengineer.tlsbunny.vendor.test.tls13.wolfssl;

import com.gypsyengineer.tlsbunny.tls13.server.Server;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.BaseDockerServer;

public class WolfsslServer extends BaseDockerServer implements Server {

    private static final int defaultPort = 40101;

    private static final String defaultServerCert = "certs/server-cert.pem";
    private static final String defaultServerKey = "certs/server-key.pem";

    private static final String image = System.getProperty(
            "tlsbunny.wolfssl.docker.image",
            "tlsbunny_wolfssl_tls13");

    public static WolfsslServer wolfsslServer() {
        return new WolfsslServer();
    }

    private WolfsslServer() {
        super(new OutputListenerImpl(
                "wolfSSL Leaving SSL_new, return 0",
                        "wolfSSL Entering wolfSSL_SetHsDoneCb"),
                defaultPort, image);
        output.prefix("wolfssl-server");
        options.put("-p", String.valueOf(defaultPort));
        options.put("-v", "4"); // TLS 1.3 only
        options.put("-d", no_arg); // no client auth
        options.put("-i", no_arg);
        options.put("-g", no_arg);
        options.put("-b", no_arg);
        options.put("-x", no_arg);
        options.put("-c", defaultServerCert);
        options.put("-k", defaultServerKey);
    }

    public WolfsslServer clientAuth() {
        options.remove("-d");
        return this;
    }

    @Override
    public int port() {
        return defaultPort;
    }

    @Override
    protected String killCommand() {
        return "rm /var/src/wolfssl/stop.file; pidof lt-server | xargs kill -9";
    }
}
