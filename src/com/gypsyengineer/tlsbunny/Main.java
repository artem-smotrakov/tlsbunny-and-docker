package com.gypsyengineer.tlsbunny;

import com.gypsyengineer.tlsbunny.tls13.test.client.MutatedClient;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.Utils;
import static com.gypsyengineer.tlsbunny.utils.Utils.achtung;
import static com.gypsyengineer.tlsbunny.utils.Utils.info;
import static com.gypsyengineer.tlsbunny.utils.Utils.printf;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Main entry point.
 */
public class Main {

    private static void help() {
        printf("Available parameters (via system properties):%n");
        printf("    %s%n", Config.helpHost());
        printf("    %s%n", Config.helpPort());
        printf("    %s%n", Config.helpStartTest());
        printf("    %s%n", Config.helpTotal());
        printf("    %s%n", Config.helpParts());
        printf("    %s%n", Config.helpRatios());
        printf("    %s%n", Config.helpThreads());
        printf("    %s%n", Config.helpTarget());
        printf("    %s%n", Config.helpFuzzer());
        printf("    %s%n", Config.helpMode());

        printf("Available fuzzers:%n    %s%n",
                String.join(", ", Config.getAvailableFuzzers()));
    }

    private static boolean needHelp(String[] args) {
        return args.length > 0 && Utils.contains(args[0], "help", "-help", "--help");
    }

    public static void main(String[] args) throws Exception {
        if (needHelp(args)) {
            help();
            return;
        }

        String host = Config.Instance.getHost();
        int port = Config.Instance.getPort();
        info("okay, we're going to fuzz %s:%d", host, port);

        double minRatio = Config.Instance.getMinRatio();
        double maxRatio = Config.Instance.getMaxRatio();

        int threads = Config.Instance.getThreads();

        info("we are going to use %d threads", threads);
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        try {
            List<Config.FuzzerConfig> fuzzerConfigs = Config.Instance.getFuzzerConfigs();
            info("found %d fuzzer configs", fuzzerConfigs.size());
            for (Config.FuzzerConfig fuzzerConfig : fuzzerConfigs) {
                switch (fuzzerConfig.getFuzzer()) {
                    case "MutatedClient":
                        int total = fuzzerConfig.getTotal();
                        int parts = fuzzerConfig.getParts();
                        int testsNumber = total / parts;

                        // TODO: get a start test from a fuzzer config
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
