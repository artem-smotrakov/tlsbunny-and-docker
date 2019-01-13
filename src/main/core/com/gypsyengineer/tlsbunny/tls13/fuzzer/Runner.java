package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.tls13.utils.SplittedFuzzerConfig;
import com.gypsyengineer.tlsbunny.tls13.connection.Analyzer;
import com.gypsyengineer.tlsbunny.tls13.connection.check.Check;
import com.gypsyengineer.tlsbunny.tls13.utils.FuzzerConfig;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Runner {

    private FuzzerConfig[] fuzzerConfigs;
    private ClientFactory fuzzerFactory;
    private Output output;
    private Check[] checks;
    private Analyzer analyzer;

    public Runner set(FuzzerConfig... fuzzerConfigs) {
        this.fuzzerConfigs = fuzzerConfigs;
        return this;
    }

    public Runner set(ClientFactory fuzzerFactory) {
        this.fuzzerFactory = fuzzerFactory;
        return this;
    }

    public Runner set(Output output) {
        this.output = output;
        return this;
    }

    public Runner set(Check... checks) {
        this.checks = checks;
        return this;
    }

    public Runner set(Analyzer analyzer) {
        this.analyzer = analyzer;
        return this;
    }

    public void submit() throws InterruptedException {
        int threads = 1;
        for (FuzzerConfig fuzzerConfig : fuzzerConfigs) {
            if (threads < fuzzerConfig.threads()) {
                threads = fuzzerConfig.threads();
            }
        }
        output.info("we are going to use %d threads", threads);

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        try {
            printConfigs();

            int index = 0;
            for (FuzzerConfig fuzzerConfig : fuzzerConfigs) {
                for (FuzzerConfig subConfig : split(fuzzerConfig)) {
                    output.prefix(String.format("part-%d", index++));
                    executor.submit(fuzzerFactory.create(subConfig, output));
                }
            }
        } finally {
            executor.shutdown();
        }

        // we are so patient ...
        executor.awaitTermination(365, TimeUnit.DAYS);

        output.info("phew, we are done with fuzzing!");

        if (analyzer != null) {
            analyzer.set(output).run();
        }
    }

    private FuzzerConfig[] split(FuzzerConfig config) {
        FuzzerConfig[] configs = new FuzzerConfig[config.parts()];
        long perThread = (config.endTest() - config.startTest()) / config.parts();
        long start = config.startTest();
        long end = start + perThread;

        for (int i = 0; i < config.parts(); i++) {
            configs[i] = new SplittedFuzzerConfig(config, start, end);
            start = end;
            end = start + perThread;
        }

        return configs;
    }

    private void printConfigs() {
        output.info("we're going to submit the following configs");
        int i = 1;
        for (FuzzerConfig config : fuzzerConfigs) {
            output.info("fuzzer config (%d):", i);
            StructFactory factory = config.factory();
            output.info("\tfactory    = %s", factory.getClass().getSimpleName());
            if (factory instanceof FuzzyStructFactory) {
                FuzzyStructFactory fuzzyStructFactory = (FuzzyStructFactory) factory;
                output.info("\tfuzzer     = %s", fuzzyStructFactory.fuzzer());
            }
            if (factory instanceof DeepHandshakeFuzzer) {
                DeepHandshakeFuzzer deepHandshakeFuzzer = (DeepHandshakeFuzzer) factory;
                output.info("\tfuzzer     = %s", deepHandshakeFuzzer.fuzzer());
            }
            output.info("\tstart test = %d", config.startTest());
            output.info("\tend test   = %d", config.endTest());
            output.info("\tparts      = %d", config.parts());
            i++;
        }
    }

    public interface ClientFactory {
        Runnable create(FuzzerConfig config, Output output);
    }

}
