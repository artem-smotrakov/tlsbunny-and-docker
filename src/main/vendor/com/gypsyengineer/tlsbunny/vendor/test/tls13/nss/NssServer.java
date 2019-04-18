package com.gypsyengineer.tlsbunny.vendor.test.tls13.nss;

import com.gypsyengineer.tlsbunny.tls13.server.Server;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.BaseDockerServer;

public class NssServer extends BaseDockerServer implements Server {

    private static final int defaultPort = 60101;

    private static final String image = System.getProperty(
            "tlsbunny.nss.docker.image",
            "tlsbunny_nss_tls13");

    public static NssServer nssServer() {
        return new NssServer();
    }

    private NssServer() {
        super(new OutputListenerImpl(
                "selfserv: ", "About to call accept."),
                defaultPort, image);
        output.prefix("nss-server");
        options.put("-v", no_arg);
        options.put("-d", "sql:./nssdb");
        options.put("-p", String.valueOf(defaultPort));
        options.put("-V", "tls1.3:tls1.3");
        options.put("-H", "1");
        options.put("-n", "localhost");
    }

    public NssServer clientAuth() {
        options.put("-r", no_arg);
        return this;
    }

    @Override
    public int port() {
        return defaultPort;
    }

    @Override
    protected String killCommand() {
        return "pidof selfserv | xargs kill -SIGINT";
    }
}
