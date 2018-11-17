package com.gypsyengineer.tlsbunny.impl.test.tls13;

import com.gypsyengineer.tlsbunny.impl.test.tls13.openssl.server.OpensslServer;
import com.gypsyengineer.tlsbunny.tls13.client.Client;
import com.gypsyengineer.tlsbunny.tls13.server.Server;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.gypsyengineer.tlsbunny.utils.Achtung.achtung;

public class Utils {

    public static final int delay = 500; // in millis
    public static final int default_timeout = 3 * 60 * 1000; // 3 minutes, in millis

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    public static Process exec(Output output, String template, Object... params)
            throws IOException {

        String command = String.format(template, params);
        output.info("run: %s", command);

        ProcessBuilder pb = new ProcessBuilder(
                command.split("\\s+"));
        pb.redirectErrorStream(true);

        return pb.start();
    }

    public static Process exec(Output output, List<String> command) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (String part : command) {
            sb.append(part).append(" ");
        }
        output.info("run: %s", sb.toString());

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);

        return pb.start();
    }

    public static void waitStart(Client client)
            throws IOException, InterruptedException {

        waitStart(client, default_timeout);
    }

    public static void waitStart(Client client, long timeout)
            throws IOException, InterruptedException {

        long start = System.currentTimeMillis();
        do {
            Thread.sleep(delay);
            if (timeout > 0 && System.currentTimeMillis() - start > timeout) {
                throw new IOException(
                        "timeout reached while waiting for the client to start");
            }
        } while (!client.running());
    }

    public static void waitStart(Server server)
            throws IOException, InterruptedException {

        waitStart(server, default_timeout);
    }

    public static void waitStart(Server server, long timeout)
            throws IOException, InterruptedException {

        long start = System.currentTimeMillis();
        do {
            Thread.sleep(delay);
            if (timeout > 0 && System.currentTimeMillis() - start > timeout) {
                throw new IOException(
                        "timeout reached while waiting for the server to start");
            }
        } while (!server.running());
    }

    public static void waitStop(Client client)
            throws IOException, InterruptedException {

        waitStop(client, default_timeout);
    }

    public static void waitStop(Client client, long timeout)
            throws InterruptedException, IOException {

        long start = System.currentTimeMillis();
        do {
            Thread.sleep(delay);
            if (timeout > 0 && System.currentTimeMillis() - start > timeout) {
                throw new IOException(
                        "timeout reached while waiting for the client to stop");
            }
        } while (client.running());
    }

    public static void waitStop(Server server)
            throws IOException, InterruptedException {

        waitStop(server, default_timeout);
    }

    public static void waitStop(Server server, long timeout)
            throws InterruptedException, IOException {

        long start = System.currentTimeMillis();
        do {
            Thread.sleep(delay);
            if (timeout > 0 && System.currentTimeMillis() - start > timeout) {
                throw new IOException(
                        "timeout reached while waiting for the server to stop");
            }
        } while (server.running());
    }

    public static void waitServerReady(OpensslServer server)
            throws IOException, InterruptedException {

        waitServerReady(server, default_timeout);
    }

    public static void waitServerReady(OpensslServer server, long timeout)
            throws IOException, InterruptedException {

        long start = System.currentTimeMillis();
        do {
            Thread.sleep(delay);
            if (timeout > 0 && System.currentTimeMillis() - start > timeout) {
                throw new IOException(
                        "timeout reached while waiting for the server to start");
            }
        } while (!server.ready());
    }

    public static int waitProcessFinish(Output output, List<String> command)
            throws IOException, InterruptedException {

        return waitProcessFinish(output, exec(output, command));
    }

    public static int waitProcessFinish(
            Output output, String template, Object... params)
                throws IOException, InterruptedException {

        return waitProcessFinish(output, exec(output, template, params));
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

        output.flush();

        return process.exitValue();
    }

    public static void checkForASanFindings(Output output) {
        for (String string : output.strings()) {
            if (string.contains("ERROR: AddressSanitizer:")) {
                throw achtung("hey, AddressSanitizer found something!");
            }
        }
    }
}
