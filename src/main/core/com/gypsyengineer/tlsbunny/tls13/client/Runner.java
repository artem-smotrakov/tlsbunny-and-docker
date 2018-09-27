package com.gypsyengineer.tlsbunny.tls13.client;

import com.gypsyengineer.tlsbunny.tls13.connection.Analyzer;
import com.gypsyengineer.tlsbunny.tls13.utils.FuzzerConfig;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.gypsyengineer.tlsbunny.utils.Utils.info;

public class Runner {

    public interface FuzzerFactory {
        Runnable create(FuzzerConfig config, Output output);
    }

    private Config mainConfig;
    private FuzzerConfig[] fuzzerConfigs;
    private FuzzerFactory fuzzerFactory;
    private Client client;
    private Output output;
    private Analyzer analyzer;
    private int index;

    public Runner set(Config mainConfig) {
        this.mainConfig = mainConfig;
        return this;
    }

    public Runner set(FuzzerConfig... fuzzerConfigs) {
        this.fuzzerConfigs = fuzzerConfigs;
        return this;
    }

    public Runner set(FuzzerFactory fuzzerFactory) {
        this.fuzzerFactory = fuzzerFactory;
        return this;
    }

    public Runner set(Client client) {
        this.client = client;
        return this;
    }

    public Runner set(Output output) {
        this.output = output;
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

        info("we are going to use %d threads", threads);

        String targetFilter = mainConfig.targetFilter();
        if (!targetFilter.isEmpty()) {
            info("target filter: %s", targetFilter);
        }

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        try {
            index = 0;
            for (FuzzerConfig fuzzerConfig : fuzzerConfigs) {
                if (skip(fuzzerConfig)) {
                    info("skip config with target '%s'",
                            fuzzerConfig.factory().target());
                    continue;
                }

                fuzzerConfig.set(mainConfig);
                fuzzerConfig.client(client);

                if (analyzer != null) {
                    fuzzerConfig.analyzer(analyzer);
                }

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

        info("phew, we are done!");

        if (analyzer != null) {
            analyzer.set(output).run();
        }
    }

    private boolean skip(FuzzerConfig config) {
        String targetFilter = mainConfig.targetFilter();
        if (!targetFilter.isEmpty()) {
            return !config.factory().target().toString().contains(targetFilter);
        }

        return false;
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

}
