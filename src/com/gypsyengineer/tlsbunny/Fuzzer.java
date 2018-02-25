package com.gypsyengineer.tlsbunny;

import com.gypsyengineer.tlsbunny.tls13.client.MutatedClient;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.Utils;
import static com.gypsyengineer.tlsbunny.utils.Utils.achtung;
import static com.gypsyengineer.tlsbunny.utils.Utils.info;
import static com.gypsyengineer.tlsbunny.utils.Utils.printf;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Main entry point.
 */
public class Fuzzer {

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

        // TODO: need to implement setting targets in command line
        // printf("Available targets:%n    %s%n",
        //        String.join(", ", MutatedStructFactory.getAvailableTargets()));
    }

    private static boolean needHelp(String[] args) {
        return args.length > 0 && Utils.contains(args[0], "help", "-help", "--help");
    }

    public static void main(String[] args) throws Exception {
        if (needHelp(args)) {
            help();
            return;
        }

        // TODO: need to implement setting targets in command line
        //String[] availableTargets = MutatedStructFactory.getAvailableTargets();
        //info("fuzzer has the following targets:%n    %s",
        //        String.join(", ", availableTargets));

        String host = Config.Instance.getHost();
        int port = Config.Instance.getPort();
        info("okay, we're going to fuzz %s:%d", host, port);

        // TODO: it would be nice if we could configure fuzzers in command line
        //       without writing a config file
        //       to do that, we need to specify targets in command line
        // String[] targets = Config.getTargets();

        int total = Config.Instance.getTotal();
        int parts = Config.Instance.getParts();

        double minRatio = Config.Instance.getMinRatio();
        double maxRatio = Config.Instance.getMaxRatio();

        // TODO: config file should contain a common mode like "mode: bit_flip"

        ExecutorService executor = Executors.newFixedThreadPool(Config.Instance.getThreads());
        try {
            for (Config.FuzzerConfig fuzzerConfig : Config.Instance.getFuzzerConfigs()) {
                switch (fuzzerConfig.getFuzzer()) {
                    case "MutatedClient":
                        int testsNumber = total / parts;
                        int startTest = Config.Instance.getStartTest();
                        while (startTest < testsNumber * parts) {
                            info("start fuzzer: %s", fuzzerConfig.getFuzzer());
                            info("      target: %s", fuzzerConfig.getTarget());
                            info("  first test: %d", startTest);
                            info(" total tests: %d", testsNumber);

                            MutatedClient.runFuzzer(executor, host, port,
                                    fuzzerConfig.getTarget(),
                                    fuzzerConfig.getMode(),
                                    minRatio, maxRatio,
                                    startTest, testsNumber);
                            startTest += testsNumber;
                        }

                        MutatedClient.runFuzzer(executor, host, port,
                                fuzzerConfig.getTarget(),
                                fuzzerConfig.getMode(),
                                minRatio, maxRatio,
                                startTest, total % parts);
                        break;
                    default:
                        achtung("Unknown fuzzer: %s", fuzzerConfig.getFuzzer());
                }
            }
        } finally {
            executor.shutdown();
        }

        // we are so patient ...
        executor.awaitTermination(365, TimeUnit.DAYS);

        info("phew, we are done!");
    }
}
