package com.gypsyengineer.tlsbunny.vendor.test.tls13.fizz;

import com.gypsyengineer.tlsbunny.tls13.server.Server;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.BaseDockerServer;

public class FizzServer extends BaseDockerServer implements Server {

    private static final int defaultPort = 30101;

    private static final String image = System.getProperty(
            "tlsbunny.fizz.docker.image",
            "tlsbunny_fizz_tls13");

    public static FizzServer fizzServer() {
        return new FizzServer();
    }

    private FizzServer() {
        super(new OutputListenerImpl(
                "Started listening", "Ready to accept"),
                defaultPort, image);
        output.prefix("fizz-server");
        option("server");
        option("-accept", String.valueOf(defaultPort));
        option("-loop");
        option("-http");
        option("-cert", "certs/server_cert.pem");
        option("-key", "certs/server_key.pem");
    }

    @Override
    public int port() {
        return defaultPort;
    }

    @Override
    protected String killCommand() {
        return "pidof fizz | xargs kill -SIGINT";
    }
}
