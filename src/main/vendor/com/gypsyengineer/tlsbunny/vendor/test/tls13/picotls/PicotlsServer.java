package com.gypsyengineer.tlsbunny.vendor.test.tls13.picotls;

import com.gypsyengineer.tlsbunny.tls13.server.Server;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.BaseDockerServer;

public class PicotlsServer extends BaseDockerServer implements Server {

    private static final int defaultPort = 20101;

    private static final String image = System.getProperty(
            "tlsbunny.picotls.docker.image",
            "tlsbunny_picotls_tls13");

    public static PicotlsServer picotlsServer() {
        return new PicotlsServer();
    }

    private PicotlsServer() {
        super(new OutputListenerImpl(
                "server started on port", "waiting for connections"));
        output.prefix("picotls-server");
        options.put("-c", "certs/server_cert.pem");
        options.put("-k", "certs/server_key.pem");
        options.put("-i", "message");
        options.put("0.0.0.0", no_arg);
        options.put(String.valueOf(defaultPort), no_arg);
    }

    public PicotlsServer clientAuth() {
        options.put("-a", no_arg);
        return this;
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
        return "pidof cli | xargs kill -SIGINT";
    }
}
