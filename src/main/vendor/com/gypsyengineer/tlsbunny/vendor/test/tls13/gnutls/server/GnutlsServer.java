package com.gypsyengineer.tlsbunny.vendor.test.tls13.gnutls.server;

import com.gypsyengineer.tlsbunny.tls13.server.Server;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.BaseDockerServer;

public class GnutlsServer extends BaseDockerServer implements Server {

    private static final int defaultPort = 50101;

    private static final String image = System.getProperty(
            "tlsbunny.gnutls.docker.image",
            "tlsbunny_gnutls_tls13");

    public static GnutlsServer gnutlsServer() {
        return new GnutlsServer();
    }

    private GnutlsServer() {
        super(new OutputListenerImpl(
                "HTTP Server listening on", "server is ready to accept"));
        output.prefix("gnutls-server");
        options.put("--port", String.valueOf(defaultPort));
        options.put("--http", no_arg);
        options.put("--x509cafile", "certs/root_cert.pem");
        options.put("--x509keyfile", "certs/server_key.pem");
        options.put("--x509certfile", "certs/server_cert.pem");
        options.put("--disable-client-cert", no_arg);
        options.put("--priority", "NORMAL:-VERS-ALL:+VERS-TLS1.3:+VERS-TLS1.2");
        dockerEnv.put("ASAN_OPTIONS", "detect_leaks=0");
    }

    @Override
    public int port() {
        return defaultPort;
    }

    @Override
    protected int dockerPort() {
        return defaultPort;
    }

    @Override
    protected String dockerImage() {
        return image;
    }

    @Override
    protected String killCommand() {
        return "rm /var/src/tlsbunny/stop.file; pidof gnutls-serv | xargs kill -SIGINT";
    }
}
