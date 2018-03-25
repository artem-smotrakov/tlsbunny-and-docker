package com.gypsyengineer.tlsbunny.tls13.test.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.gypsyengineer.tlsbunny.utils.Utils.info;

public class MultipleThreads {

    public static void submit(long start, long total, int parts, int threads, ThreadFactory factory)
            throws InterruptedException {

        info("we are going to use %d threads", threads);
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        try {
            long end = start + total;
            long perThread = total / parts;

            long test = start;
            long limit = start + perThread;
            while (limit < end) {
                executor.submit(factory.create(test, limit));

                test = limit;
                limit += perThread;
            }

            if (test < end) {
                executor.submit(factory.create(test, end));
            }
        } finally {
            executor.shutdown();
        }

        // we are so patient ...
        executor.awaitTermination(365, TimeUnit.DAYS);

        info("phew, we are done!");
    }

    interface ThreadFactory {
        Runnable create(long test, long limit);
    }
}
