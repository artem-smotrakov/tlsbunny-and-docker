package com.gypsyengineer.tlsbunny.vendor.test.tls13.openssl;

import com.gypsyengineer.tlsbunny.vendor.test.tls13.BaseDockerServer;
import com.gypsyengineer.tlsbunny.tls13.server.Server;

public class OpensslServer extends BaseDockerServer implements Server {

    private static final int defaultPort = 10101;

    private static final String image = System.getProperty(
            "tlsbunny.openssl.docker.image",
            "tlsbunny_openssl_tls13");

    public static OpensslServer opensslServer() {
        return new OpensslServer();
    }

    private OpensslServer() {
        super(new OutputListenerImpl("ACCEPT", "tlsbunny: accept"),
                defaultPort, image);
        output.prefix("openssl-server");
        options.put("-key", "certs/server_key.pem");
        options.put("-cert", "certs/server_cert.der");
        options.put("-certform", "der");
        options.put("-accept", String.valueOf(defaultPort));
        options.put("-www", no_arg);
        options.put("-tls1_3", no_arg);
    }

    public OpensslServer noTLSv13() {
        options.remove("-tls1_3");
        return this;
    }

    public OpensslServer minTLSv1() {
        return minProtocol("TLSv1");
    }

    public OpensslServer maxTLSv13() {
        return maxProtocol("TLSv1.3");
    }

    public OpensslServer minProtocol(String value) {
        options.put("-min_protocol", value);
        return this;
    }

    public OpensslServer enableDebugOutput() {
        options.put("-debug", no_arg);
        return this;
    }

    public OpensslServer enableExtDebugOutput() {
        options.put("-tlsextdebug", no_arg);
        return this;
    }

    public OpensslServer maxProtocol(String value) {
        options.put("-max_protocol", value);
        return this;
    }

    public OpensslServer clientAuth() {
        options.put("-Verify", "0");
        options.put("-CAfile", "certs/root_cert.pem");
        return this;
    }

    @Override
    public int port() {
        return defaultPort;
    }

    @Override
    protected String killCommand() {
        return "pidof openssl | xargs kill -SIGINT";
    }
}
