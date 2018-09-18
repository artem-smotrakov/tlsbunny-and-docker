package com.gypsyengineer.tlsbunny.impl.test.tls13;

import com.gypsyengineer.tlsbunny.tls13.server.Server;

import java.io.IOException;
import java.net.ServerSocket;

public class Utils {

    public static final int delay = 500; // in millis
    public static final int default_server_start_timeout = 10 * 1000; // im millis
    public static final int default_server_stop_timeout  = 10 * 1000; // im millis

    public static int getFreePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    public static Process exec(String template, Object... params)
            throws IOException {

        return new ProcessBuilder(String.format(template, params).split("\\s+")).start();
    }

    public static void waitServerStart(Server server) throws Exception {
        waitServerStart(server, default_server_start_timeout);
    }

    public static void waitServerStart(Server server, long timeout)
            throws Exception {

        long start = System.currentTimeMillis();
        do {
            Thread.sleep(delay);
            if (System.currentTimeMillis() - start > timeout) {
                throw new Exception(
                        "timeout reached while waiting for the server to start");
            }
        } while (!server.running());
    }

    public static void waitServerStop(Server server) throws Exception {
        waitServerStop(server, default_server_stop_timeout);
    }

    public static void waitServerStop(Server server, long timeout)
            throws Exception {

        long start = System.currentTimeMillis();
        do {
            Thread.sleep(delay);
            if (System.currentTimeMillis() - start > timeout) {
                throw new RuntimeException(
                        "timeout reached while waiting for the server to stop");
            }
        } while (server.running());
    }
}
