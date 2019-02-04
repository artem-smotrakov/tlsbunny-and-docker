package com.gypsyengineer.tlsbunny.vendor.test.tls13;

import com.gypsyengineer.tlsbunny.tls13.client.Client;
import com.gypsyengineer.tlsbunny.tls13.server.Server;
import com.gypsyengineer.tlsbunny.output.Output;

import java.io.IOException;
import java.util.List;

import static com.gypsyengineer.tlsbunny.utils.Achtung.achtung;

public class Utils {

    // in millis
    private static final int delay = 500;
    private static final int start_delay = 5 * 1000;
    private static final int stop_delay  = 5 * 1000;
    private static final int default_timeout = 3 * 60 * 1000; // 3 minutes

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

    public static Process exec(List<String> command) throws IOException {
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
            Thread.sleep(start_delay);
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
            Thread.sleep(stop_delay);
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

    public static void waitServerReady(Server server)
            throws IOException, InterruptedException {

        waitServerReady(server, default_timeout);
    }

    public static void waitServerReady(Server server, long timeout)
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

        return exec(output, command).waitFor();
    }

    public static int waitProcessFinish(
            Output output, String template, Object... params)
                throws IOException, InterruptedException {

        return exec(output, template, params).waitFor();
    }

    public static void checkForASanFindings(Output output) {
        for (String string : output.lines()) {
            if (string.contains("ERROR: AddressSanitizer:")) {
                throw achtung("hey, AddressSanitizer found something!");
            }
        }
    }
}
