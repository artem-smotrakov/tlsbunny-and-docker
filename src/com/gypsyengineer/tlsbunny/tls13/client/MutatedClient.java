package com.gypsyengineer.tlsbunny.tls13.client;

import com.gypsyengineer.tlsbunny.tls13.crypto.ApplicationDataChannel;
import com.gypsyengineer.tlsbunny.tls13.fuzzer.MutatedStructFactory;
import com.gypsyengineer.tlsbunny.tls13.handshake.ClientHandshaker;
import com.gypsyengineer.tlsbunny.tls13.handshake.ECDHENegotiator;
import com.gypsyengineer.tlsbunny.tls13.struct.CipherSuite;
import com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup;
import com.gypsyengineer.tlsbunny.tls13.struct.SignatureScheme;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.utils.CertificateHolder;
import com.gypsyengineer.tlsbunny.utils.Connection;
import com.gypsyengineer.tlsbunny.utils.Output;
import com.gypsyengineer.tlsbunny.utils.Parameters;
import com.gypsyengineer.tlsbunny.utils.Utils;
import static com.gypsyengineer.tlsbunny.utils.Utils.info;
import static com.gypsyengineer.tlsbunny.utils.Utils.printf;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MutatedClient implements Runnable {

    public static final int ONE_TEST = 1;
    public static final int DEFAULT_TESTS_NUMBER = 10000;
    public static final byte[] HTTP_GET_REQUEST = "GET / HTTP/1.1\n\n".getBytes();

    private final MutatedStructFactory fuzzer;
    private final Output output;
    private final String host;
    private final int port;
    private final int testsNumber;

    public MutatedClient(MutatedStructFactory fuzzer, Output output,
            String host, int port, int testsNumber) {

        this.fuzzer = fuzzer;
        this.output = output;
        this.host = host;
        this.port = port;
        this.testsNumber = testsNumber;
    }

    @Override
    public void run() {
        try {
            ClientHandshaker handshaker = ClientHandshaker.create(
                    fuzzer,
                    SignatureScheme.ecdsa_secp256r1_sha256,
                    NamedGroup.secp256r1,
                    ECDHENegotiator.create(NamedGroup.secp256r1, fuzzer),
                    CipherSuite.TLS_AES_128_GCM_SHA256,
                    CertificateHolder.NO_CERTIFICATE);

            int test = 0;
            String threadName = Thread.currentThread().getName();
            while (fuzzer.canFuzz() && test < testsNumber) {
                output.info("%s, test %d of %d", threadName, test, testsNumber);
                output.info("now fuzzer's state is '%s'", fuzzer.getState());
                try (Connection connection = Connection.create(host, port)) {
                    output.info("start handshaking");
                    ApplicationDataChannel applicationData = handshaker.start(connection);

                    if (handshaker.receivedAlert()) {
                        output.info("received %s", handshaker.getReceivedAlert());
                        continue;
                    }

                    if (applicationData == null) {
                        // TODO: check what happened
                        output.info("handshake failed, couldn't create application data channel");
                        continue;
                    }

                    applicationData.send(HTTP_GET_REQUEST);
                    byte[] bytes = applicationData.receive();
                    output.info("we just received %d bytes:%n%s",
                            bytes.length, new String(bytes));
                    output.achtung("holy moly, connection succeeded!");
                } finally {
                    output.flush();
                    test++;
                    fuzzer.moveOn();
                }
            }
        } catch (Exception e) {
            output.achtung("what the hell? unexpected exception: %s", e.getMessage());
        } finally {
            output.flush();
        }
    }

    private static void help() {
        printf("Available parameters (via system properties):%n");
        printf("    %s%n", Parameters.helpHost());
        printf("    %s%n", Parameters.helpPort());
        printf("    %s%n", Parameters.helpMode());
        printf("    %s%n", Parameters.helpTestsNumber());
        printf("    %s%n", Parameters.helpRatios());
        printf("    %s%n", Parameters.helpTargets());
        printf("Available targets:%n    %s%n",
                String.join(", ", MutatedStructFactory.getAvailableTargets()));
    }

    private static boolean needHelp(String[] args) {
        return args.length > 0 && Utils.contains(args[0], "help", "-help", "--help");
    }

    public static void main(String[] args) throws Exception {
        if (needHelp(args)) {
            help();
            return;
        }

        String[] availableTargets = MutatedStructFactory.getAvailableTargets();
        info("fuzzer has the following targets:%n    %s",
                String.join(", ", availableTargets));

        String host = Parameters.getHost();
        int port = Parameters.getPort();
        info("okay, we're going to fuzz %s:%d", host, port);

        String[] targets = Parameters.getTargets();
        String mode = Parameters.getMode();
        String state = Parameters.getState();
        int totalTestsNumber = Parameters.getTestsNumber();
        int threads = Parameters.getThreads();

        if (!state.isEmpty()) {
            info("okay, reproduce the following test %s", state);

            try (Output output = new Output()) {
                MutatedStructFactory fuzzer = new MutatedStructFactory(
                            StructFactory.getDefault(),
                            output,
                            Parameters.getMinRatio(),
                            Parameters.getMaxRatio());

                fuzzer.setState(state);

                new MutatedClient(fuzzer, output, host, port, ONE_TEST).run();
            }

            info("phew, we are done!");
            return ;
        }

        if (targets.length != 0) {
            info("okay, we're going to fuzz the following targets:%n    %s",
                    String.join(",", targets));
            info("we're going to run %d tests for each target in %d threads",
                    totalTestsNumber, threads);
        }

        if (!mode.isEmpty()) {
            info("okay, let's set fuzzing mode to '%s'", mode);
        }

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        try {
            for (String target : targets) {
                info("we're going to fuzz target '%s'", target);

                int testsNumber = totalTestsNumber / threads;
                int startTest = 0;
                while (startTest < testsNumber * threads) {
                    runFuzzer(executor, host, port, target, mode,
                            startTest, testsNumber);
                    startTest += testsNumber;
                }

                runFuzzer(executor, host, port, target, mode,
                        startTest, totalTestsNumber % threads);
            }
        } finally {
            executor.shutdown();
        }

        executor.awaitTermination(365, TimeUnit.DAYS);
        info("phew, we are done!");
    }

    private static void runFuzzer(ExecutorService executor,
            String host, int port,
            String target, String mode,
            int startTest, int testsNumber) {

        Output output = new Output();
        MutatedStructFactory fuzzer = new MutatedStructFactory(
                        StructFactory.getDefault(),
                        output,
                        Parameters.getMinRatio(),
                        Parameters.getMaxRatio());

        fuzzer.setTarget(target);
        if (!mode.isEmpty()) {
            fuzzer.setMode(mode);
        }

        fuzzer.setTest(startTest);

        executor.submit(new MutatedClient(fuzzer, output,
                host, port, testsNumber));
    }
}
