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
import com.gypsyengineer.tlsbunny.utils.Parameters;
import static com.gypsyengineer.tlsbunny.utils.Utils.achtung;
import static com.gypsyengineer.tlsbunny.utils.Utils.info;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MutationClientFuzzer implements Runnable {

    public static final int DEFAULT_TESTS_NUMBER = 10000;
    public static final byte[] HTTP_GET_REQUEST = "GET / HTTP/1.1\n\n".getBytes();

    private final MutatedStructFactory fuzzer;
    private final String host;
    private final int port;
    private final int testsNumber;

    public MutationClientFuzzer(MutatedStructFactory fuzzer,
            String host, int port, int testsNumber) {

        this.fuzzer = fuzzer;
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
            while (fuzzer.canFuzz() && test < testsNumber) {
                info("test #%d: now fuzzer's state is '%s'", test, fuzzer.getState());
                try (Connection connection = Connection.create(host, port)) {
                    info("start handshaking");
                    ApplicationDataChannel applicationData = handshaker.start(connection);

                    if (handshaker.receivedAlert()) {
                        info("received %s", handshaker.getReceivedAlert());
                        continue;
                    }

                    if (applicationData == null) {
                        // TODO: check what happened
                        info("handshake failed, couldn't create application data channel");
                        continue;
                    }

                    applicationData.send(HTTP_GET_REQUEST);
                    byte[] bytes = applicationData.receive();
                    info("we just received %d bytes:%n%s",
                            bytes.length, new String(bytes));
                    achtung("holy moly, connection succeeded!");
                } finally {
                    test++;
                    fuzzer.moveOn();
                }
            }
        } catch (Exception e) {
            achtung("what the hell? unexpected exception: %s", e.getMessage());
        }
    }

    private static void help() {
        System.out.printf("Available parameters (via system properties):%n");
        System.out.printf("    %s%n", Parameters.helpHost());
        System.out.printf("    %s%n", Parameters.helpPort());
        System.out.printf("    %s%n", Parameters.helpMode());
        System.out.printf("    %s%n", Parameters.helpTestsNumber());
        System.out.printf("    %s%n", Parameters.helpRatios());
        System.out.printf("    %s%n", Parameters.helpTargets());
        System.out.println();
        System.out.printf("Available targets:%n    %s%n",
                String.join(", ", MutatedStructFactory.getAvailableTargets()));
    }

    public static void main(String[] args) throws Exception {
        if (args.length > 0
                && ("help".equals(args[0]) || "-help".equals("-help") || "--help".equals(args[0]))) {

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
            // TODO: run one single test
            info("okay, let's set fuzzer's state to %s", state);
            throw new UnsupportedOperationException();
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
    }

    private static void runFuzzer(ExecutorService executor,
            String host, int port,
            String target, String mode,
            int startTest, int testsNumber) {

        MutatedStructFactory fuzzer = new MutatedStructFactory(
                        StructFactory.getDefault(),
                        Parameters.getMinRatio(),
                        Parameters.getMaxRatio());

        fuzzer.setTarget(target);
        if (!mode.isEmpty()) {
            fuzzer.setMode(mode);
        }

        fuzzer.setTest(startTest);

        executor.submit(new MutationClientFuzzer(fuzzer, host, port, testsNumber));
    }
}
