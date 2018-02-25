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
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.Connection;
import com.gypsyengineer.tlsbunny.utils.Output;
import com.gypsyengineer.tlsbunny.utils.Utils;
import static com.gypsyengineer.tlsbunny.utils.Utils.achtung;
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
        printf("    %s%n", Config.helpHost());
        printf("    %s%n", Config.helpPort());
        printf("    %s%n", Config.helpStartTest());
        printf("    %s%n", Config.helpTotal());
        printf("    %s%n", Config.helpParts());
        printf("    %s%n", Config.helpRatios());
        printf("    %s%n", Config.helpTargets());
        printf("    %s%n", Config.helpThreads());
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

        String host = Config.Instance.getHost();
        int port = Config.Instance.getPort();
        info("okay, we're going to fuzz %s:%d", host, port);

        // TODO: it would be nice if we could configure fuzzers in command line
        //       without writing a config file
        //       to do that, we need to specify targets in command line
        // String[] targets = Config.getTargets();

        int total = Config.Instance.getTotal();
        int threads = Config.Instance.getThreads();

        // TODO: config file should contain a common mode like "mode: bit_flip"

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        try {
            for (Config.FuzzerConfig fuzzerConfig : Config.Instance.getFuzzerConfigs()) {
                switch (fuzzerConfig.getFuzzer()) {
                    case "MutatedClient":
                        info("fuzzer: %s", fuzzerConfig.getFuzzer());
                        info("target: %s", fuzzerConfig.getTarget());
                        int testsNumber = total / threads;
                        int startTest = Config.Instance.getStartTest();
                        while (startTest < testsNumber * threads) {
                            runFuzzer(executor, host, port,
                                    fuzzerConfig.getTarget(),
                                    fuzzerConfig.getMode(),
                                    startTest,
                                    testsNumber);
                            startTest += testsNumber;
                        }

                        runFuzzer(executor, host, port,
                                fuzzerConfig.getTarget(),
                                fuzzerConfig.getMode(),
                                startTest,
                                total % threads);
                        break;
                    default:
                        achtung("Unknown fuzzer: %s", fuzzerConfig.getFuzzer());
                }
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

        // TODO: we should be able to specify ratios for each fuzzer
        MutatedStructFactory fuzzer = new MutatedStructFactory(
                        StructFactory.getDefault(),
                        output,
                        Config.Instance.getMinRatio(),
                        Config.Instance.getMaxRatio());

        fuzzer.setTarget(target);
        if (!mode.isEmpty()) {
            fuzzer.setMode(mode);
        }

        fuzzer.setTest(startTest);

        executor.submit(new MutatedClient(fuzzer, output,
                host, port, testsNumber));
    }
}
