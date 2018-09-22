package com.gypsyengineer.tlsbunny.impl.test.tls13.openssl;

import com.gypsyengineer.tlsbunny.impl.test.tls13.Utils;
import com.gypsyengineer.tlsbunny.tls13.connection.Check;
import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.EngineFactory;
import com.gypsyengineer.tlsbunny.tls13.server.Server;
import com.gypsyengineer.tlsbunny.tls13.server.StopCondition;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.io.IOException;

// TODO: should we really use Server interface if we don't support many its methods?
//       would it be better to create a separate interface for external servers?
public class OpensslServer implements Server, AutoCloseable {

    public static final int port = 10101;

    private static final String server_start_template =
            "docker run -p %d:%d --name %s openssl/server/tls13";

    private static final String server_stop_template =
            "docker container kill %s";

    private static final String remove_container_template =
            "docker container rm %s";

    private static final String check_container_running_template =
            "docker container port %s";

    // TODO: add synchronization
    private String containerName;
    private boolean failed = false;
    private final Output output = new Output("openssl_server");

    @Override
    public OpensslServer set(Config config) {
        throw new UnsupportedOperationException("no configs for you!");
    }

    @Override
    public OpensslServer set(Output output) {
        return this;
    }

    @Override
    public OpensslServer set(EngineFactory engineFactory) {
        throw new UnsupportedOperationException("no engine factories for you!");
    }

    @Override
    public OpensslServer set(Check check) {
        throw new UnsupportedOperationException("no checks for you!");
    }

    @Override
    public OpensslServer stopWhen(StopCondition condition) {
        throw new UnsupportedOperationException("no stop conditions for you!");
    }

    @Override
    public Engine recentEngine() {
        throw new UnsupportedOperationException("no engines for you!");
    }

    @Override
    public int port() {
        return port;
    }

    @Override
    public boolean failed() {
        return failed;
    }

    @Override
    public Thread start() {
        if (containerName != null) {
            throw new IllegalStateException(
                    "what the hell? the server has already been started!");
        }

        Thread thread = new Thread(this);
        thread.start();

        return thread;
    }

    @Override
    public void run() {
        containerName = generateContainerName();

        try {
            int code = Utils.waitProcessFinish(output, server_start_template, port, port, containerName);
            if (code != 0) {
                output.achtung("the server exited with a non-zero exit code (%d)", code);
                failed = true;
            }
        } catch (InterruptedException | IOException e) {
            output.achtung("unexpected exception occurred", e);
            failed = true;
        }
    }

    @Override
    public OpensslServer stop() {
        if (containerName == null) {
            throw new IllegalStateException(
                    "what the hell? the server has not been started yet!");
        }

        try {
            int code = Utils.waitProcessFinish(output, server_stop_template, containerName);
            if (code != 0) {
                output.achtung("could not stop the server (exit code %d)", code);
                failed = true;
            }
        } catch (InterruptedException | IOException e) {
            output.achtung("unexpected exception occurred", e);
            failed = true;
        }

        return this;
    }

    @Override
    public boolean running() {
        if (containerName == null) {
            return false;
        }

        try {
            return Utils.exec(check_container_running_template, containerName).waitFor() == 0;
        } catch (InterruptedException | IOException e) {
            failed = true;
            throw new RuntimeException("unexpected exception occurred", e);
        }
    }

    @Override
    public void close() throws IOException, InterruptedException {
        stop();

        if (containerName != null) {
            int code = Utils.waitProcessFinish(output, remove_container_template, containerName);
            if (code != 0) {
                output.achtung("could not remove the container (exit code %d)", code);
                failed = true;
            }
        }

        output.flush();
    }

    private static String generateContainerName() {
        return String.format("%s_%d",
                OpensslServer.class.getSimpleName().toLowerCase(),
                System.currentTimeMillis());
    }

}
