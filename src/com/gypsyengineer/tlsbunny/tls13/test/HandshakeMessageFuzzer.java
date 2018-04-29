package com.gypsyengineer.tlsbunny.tls13.test;

import com.gypsyengineer.tlsbunny.tls13.connection.*;
import com.gypsyengineer.tlsbunny.tls13.fuzzer.MutatedStructFactory;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.tls13.test.CommonConfig;
import com.gypsyengineer.tlsbunny.tls13.test.Config;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.io.IOException;

public abstract class HandshakeMessageFuzzer implements Runnable {

    protected static final CommonConfig commonConfig = new CommonConfig();

    protected final Output output;
    protected final Config config;
    protected final MutatedStructFactory fuzzer;

    public HandshakeMessageFuzzer(Output output, Config config) {
        fuzzer = new MutatedStructFactory(
                StructFactory.getDefault(),
                output,
                config.minRatio(),
                config.maxRatio()
        );
        fuzzer.setTarget(config.target());
        fuzzer.setMode(config.mode());
        fuzzer.setStartTest(config.startTest());
        fuzzer.setEndTest(config.endTest());

        this.output = output;
        this.config = config;
    }

    @Override
    public void run() {
        try {
            output.info("run a smoke test before fuzzing");
            connect(StructFactory.getDefault()).run(new NoAlertCheck());
        } catch (Exception e) {
            output.achtung("smoke test failed: %s", e.getMessage());
            output.achtung("skip fuzzing");
            return;
        } finally {
            output.flush();
        }

        output.info("smoke test passed, start fuzzing");
        try {
            output.prefix(Thread.currentThread().getName());
            while (fuzzer.canFuzz()) {
                output.info("test %d", fuzzer.getTest());
                output.info("now fuzzer's state is '%s'", fuzzer.getState());
                try {
                    connect(fuzzer);
                } finally {
                    output.flush();
                    fuzzer.moveOn();
                }
            }
        } catch (IOException e) {
            output.info("looks like the server closed connection", e);
        } catch (Exception e) {
            output.achtung("what the hell? unexpected exception", e);
        } finally {
            output.flush();
        }
    }

    protected abstract Engine connect(StructFactory factory) throws Exception;

}
