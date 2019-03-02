package com.gypsyengineer.tlsbunny.vendor.test.tls13.openssl;

import com.gypsyengineer.tlsbunny.output.InputStreamOutput;
import com.gypsyengineer.tlsbunny.output.Output;
import com.gypsyengineer.tlsbunny.utils.Sync;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.BaseDocker;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.Utils;
import com.gypsyengineer.tlsbunny.tls13.client.Client;
import com.gypsyengineer.tlsbunny.tls13.connection.Analyzer;
import com.gypsyengineer.tlsbunny.tls13.connection.check.Check;
import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.handshake.Negotiator;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This TLS client is based on OpenSSL s_client.
 */
public class OpensslClient extends BaseDocker implements Client {

    private static final String image = System.getProperty(
            "tlsbunny.openssl.docker.image",
            "artemsmotrakov/tlsbunny_openssl_tls13");

    private static final String host_report_directory = String.format(
            "%s/openssl_report", System.getProperty("user.dir"));

    private final Config config = SystemPropertiesConfig.load();
    private Sync sync = Sync.dummy();
    private Status status = Status.not_started;

    public OpensslClient() {
        super(new InputStreamOutput());
        dockerEnv.put("mode", "client");
        dockerEnv.put("options", "-tls1_3 -CAfile certs/root_cert.pem -curves prime256v1");
    }

    @Override
    public synchronized Config config() {
        return config;
    }

    @Override
    public OpensslClient set(StructFactory factory) {
        throw new UnsupportedOperationException("no factories for you!");
    }

    @Override
    public OpensslClient set(Negotiator negotiator) {
        throw new UnsupportedOperationException("no negotiators for you!");
    }

    @Override
    public OpensslClient set(Analyzer analyzer) {
        throw new UnsupportedOperationException("no analyzers for you!");
    }

    @Override
    public OpensslClient set(Config config) {
        throw new UnsupportedOperationException("no configs for you!");
    }

    @Override
    public OpensslClient set(Output output) {
        output.achtung("you can't set output for me!");
        return this;
    }

    @Override
    public OpensslClient set(Check... checks) {
        throw new UnsupportedOperationException("no checks for you!");
    }

    @Override
    public OpensslClient set(Sync sync) {
        this.sync = sync;
        return this;
    }

    @Override
    public Engine[] engines() {
        throw new UnsupportedOperationException("no engines for you!");
    }

    @Override
    public Output output() {
        return output;
    }

    @Override
    public Thread start() {
        createReportDirectory(host_report_directory);

        Thread thread = new Thread(this);
        thread.start();

        return thread;
    }

    @Override
    public OpensslClient connect() {
        run();
        return this;
    }

    @Override
    public Status status() {
        synchronized (this) {
            return status;
        }
    }

    @Override
    public void run() {
        List<String> command = new ArrayList<>();
        command.add("docker");
        command.add("run");
        command.add("--network");
        command.add("host");
        command.add("-v");
        command.add(String.format("%s:%s",
                host_report_directory, container_report_directory));

        dockerEnv.put("port", String.valueOf(config.port()));
        for (Map.Entry entry : dockerEnv.entrySet()) {
            command.add("-e");
            command.add(String.format("%s=%s", entry.getKey(), entry.getValue()));
        }

        command.add("--name");
        command.add(containerName);
        command.add(image);

        synchronized (this) {
            status = Status.running;
        }
        try {
            Process process = Utils.exec(output, command);
            output.set(process.getInputStream());
            int code = process.waitFor();
            if (code != 0) {
                output.achtung("the client exited with a non-zero exit code (%d)", code);
            }
        } catch (InterruptedException | IOException e) {
            output.achtung("unexpected exception occurred", e);
        } finally {
            synchronized (this) {
                status = Status.done;
            }
        }
    }

    @Override
    public OpensslClient stop() {
        try {
            List<String> command = List.of(
                    "docker",
                    "exec",
                    containerName,
                    "bash",
                    "-c",
                    "rm /var/src/tlsbunny/stop.file; pidof openssl | xargs kill -9"
            );

            int code = Utils.waitProcessFinish(output, command);
            if (code != 0) {
                output.achtung("could not stop the client (exit code %d)", code);
            }
        } catch (InterruptedException | IOException e) {
            output.achtung("unexpected exception occurred", e);
        }

        return this;
    }

    /*
    @Override
    public boolean running() {
        return containerRunning();
    }
    */

    @Override
    public void close() throws Exception {
        stop();

        Utils.waitStop(this);
        output.info("client stopped");

        int code = Utils.waitProcessFinish(output, remove_container_template, containerName);
        if (code != 0) {
            output.achtung("could not remove the container (exit code %d)", code);
        }

        output.flush();
    }

}
