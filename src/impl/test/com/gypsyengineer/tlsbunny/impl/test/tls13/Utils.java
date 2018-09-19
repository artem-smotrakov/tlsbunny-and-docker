package com.gypsyengineer.tlsbunny.impl.test.tls13;

import com.gypsyengineer.tlsbunny.tls13.server.Server;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class Utils {

    public static final int delay = 500; // in millis
    public static final int default_server_start_timeout = 10 * 1000; // im millis
    public static final int default_server_stop_timeout  = 10 * 1000; // im millis

    public static Process exec(String template, Object... params)
            throws IOException {

        return new ProcessBuilder(String.format(template, params).split("\\s+"))
                .start();
    }

    public static void waitServerStart(Server server) throws Exception {
        waitServerStart(server, default_server_start_timeout);
    }

    public static void waitServerStart(Server server, long timeout)
            throws IOException, InterruptedException {

        long start = System.currentTimeMillis();
        do {
            Thread.sleep(delay);
            if (System.currentTimeMillis() - start > timeout) {
                throw new IOException(
                        "timeout reached while waiting for the server to start");
            }
        } while (!server.running());
    }

    public static void waitServerStop(Server server) throws Exception {
        waitServerStop(server, default_server_stop_timeout);
    }

    public static void waitServerStop(Server server, long timeout)
            throws InterruptedException, IOException {

        long start = System.currentTimeMillis();
        do {
            Thread.sleep(delay);
            if (System.currentTimeMillis() - start > timeout) {
                throw new IOException(
                        "timeout reached while waiting for the server to stop");
            }
        } while (server.running());
    }

    public static int waitProcessFinish(Output output, String template, Object... params)
            throws IOException, InterruptedException {

        return waitProcessFinish(output, exec(template, params));
    }

    public static int waitProcessFinish(Output output, Process process)
            throws IOException, InterruptedException {

        InputStream is = new BufferedInputStream(process.getInputStream());
        byte[] bytes = new byte[4096];
        do {
            if (is.available() > 0) {
                int len = is.read(bytes);
                output.info("%s", new String(bytes, 0, len));
                output.flush();
            }
        } while (!process.waitFor(delay, TimeUnit.MILLISECONDS));

        return process.exitValue();
    }
}
