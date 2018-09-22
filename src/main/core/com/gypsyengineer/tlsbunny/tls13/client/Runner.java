package com.gypsyengineer.tlsbunny.tls13.client;

import com.gypsyengineer.tlsbunny.tls13.connection.Analyzer;
import com.gypsyengineer.tlsbunny.tls13.utils.FuzzerConfig;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.gypsyengineer.tlsbunny.utils.Utils.info;

public class Runner {

    private final SystemPropertiesConfig systemPropertiesConfig = SystemPropertiesConfig.load();
    private final List<Holder> holders = new ArrayList<>();
    private Analyzer analyzer;
    private int index;

    public Runner add(FuzzerFactory fuzzerFactory, List<FuzzerConfig> configs) {
        for (FuzzerConfig config : configs) {
            this.holders.add(new Holder(config, fuzzerFactory));
        }

        return this;
    }

    public Runner add(FuzzerFactory fuzzerFactory, FuzzerConfig... configs) {
        return add(fuzzerFactory, Arrays.asList(configs));
    }

    public Runner set(Analyzer analyzer) {
        this.analyzer = analyzer;
        return this;
    }

    public void submit() throws InterruptedException {
        int threads = 1;
        for (Holder holder : holders) {
            if (threads < holder.fuzzerConfig.threads()) {
                threads = holder.fuzzerConfig.threads();
            }
        }

        info("we are going to use %d threads", threads);

        String targetFilter = systemPropertiesConfig.targetFilter();
        if (!targetFilter.isEmpty()) {
            info("target filter: %s", targetFilter);
        }

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        try {
            index = 0;
            for (Holder holder : holders) {
                if (skip(holder.fuzzerConfig)) {
                    info("skip config with target '%s'",
                            holder.fuzzerConfig.factory().target());
                    continue;
                }

                submit(executor, holder);
            }
        } finally {
            executor.shutdown();
        }

        // we are so patient ...
        executor.awaitTermination(365, TimeUnit.DAYS);

        info("phew, we are done!");

        if (analyzer != Analyzer.NOTHING) {
            try (Output output = new Output()) {
                analyzer.set(output);
                analyzer.run();
            }
        }
    }

    private boolean skip(FuzzerConfig config) {
        String targetFilter = systemPropertiesConfig.targetFilter();
        if (!targetFilter.isEmpty()) {
            return !config.factory().target().toString().contains(targetFilter);
        }

        return false;
    }

    private void submit(ExecutorService executor, Holder holder) {
        for (FuzzerConfig subConfig : split(holder.fuzzerConfig)) {
            Output output = new Output();
            output.prefix(String.format("part-%d", index++));
            executor.submit(holder.fuzzerFactory.create(subConfig, output));
        }
    }

    private FuzzerConfig[] split(FuzzerConfig config) {
        FuzzerConfig[] configs = new FuzzerConfig[config.parts()];
        long perThread = (config.endTest() - config.startTest()) / config.parts();
        long start = config.startTest();
        long end = start + perThread;

        for (int i = 0; i < config.parts(); i++) {
            FuzzerConfig subConfig = new SplittedFuzzerConfig(config, start, end);

            if (analyzer != Analyzer.NOTHING) {
                subConfig.analyzer(analyzer);
            }

            configs[i] = subConfig;

            start = end;
            end = start + perThread;
        }

        return configs;
    }

    public interface FuzzerFactory {
        Runnable create(FuzzerConfig config, Output output);
    }

    private static class Holder {
        private FuzzerConfig fuzzerConfig;
        private FuzzerFactory fuzzerFactory;

        Holder(FuzzerConfig fuzzerConfig, FuzzerFactory fuzzerFactory) {
            this.fuzzerConfig = fuzzerConfig;
            this.fuzzerFactory = fuzzerFactory;
        }
    }

}
