package com.gypsyengineer.tlsbunny.tls13.test.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.gypsyengineer.tlsbunny.utils.Utils.info;

public class MultipleThreads {

    private final List<Config> configs = new ArrayList<>();

    public MultipleThreads add(Config... configs) {
        for (Config config : configs) {
            this.configs.add(config);
        }

        return this;
    }

    public void submit() throws InterruptedException {
        int threads = 1;
        for (Config config : configs) {
            if (threads < config.threads()) {
                threads = config.threads();
            }
        }

        info("we are going to use %d threads", threads);
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        try {
            for (Config config : configs) {
                submit(executor, config);
            }
        } finally {
            executor.shutdown();
        }

        // we are so patient ...
        executor.awaitTermination(365, TimeUnit.DAYS);

        info("phew, we are done!");
    }

    private static void submit(ExecutorService executor, Config config) {
        for (Config subConfig : split(config)) {
            executor.submit(subConfig.create());
        }
    }

    private static Config[] split(Config config) {
        Config[] configs = new Config[config.parts()];
        long perThread = (config.endTest() - config.startTest()) / config.parts();
        long start = config.startTest();
        long end = start + perThread;

        for (int i = 0; i < config.parts(); i++) {
            Config subConfig = config.copy();
            subConfig.parts(1);
            subConfig.startTest(start);
            subConfig.endTest(end);
            configs[i] = subConfig;
        }

        return configs;
    }

}
