package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.io.IOException;

public class LegacySessionIdFuzzer implements Fuzzer<Vector<Byte>> {

    public static final int LENGTH_BYTES = 1;

    public static class FuzzedLegacySessionId extends FuzzedVector<Byte> {

        public FuzzedLegacySessionId(int encodingLength, byte[] bytes) {
            super(LENGTH_BYTES, encodingLength, bytes);
        }
    }

    private static final Generator[] generators = {
            (sessionId, output) -> {
                byte[] content = sessionId.bytes();
                output.info("set encoding length to 0, but content length is still %d",
                        content.length);
                return new FuzzedLegacySessionId(0, content);
            },
            (sessionId, output) -> {
                byte[] content = sessionId.bytes();
                output.info("set encoding length to 1, but content length is still %d",
                        content.length);
                return new FuzzedLegacySessionId(1, content);
            },
            (sessionId, output) -> {
                byte[] content = sessionId.bytes();
                int encodingLength = content.length + 1;
                output.info("set encoding length to %d, but content length is still %d",
                        encodingLength, content.length);
                return new FuzzedLegacySessionId(
                        encodingLength, content);
            }
    };

    private int state = 0;
    private int end = generators.length - 1;

    private Output output;

    @Override
    public String getState() {
        return Integer.toString(state);
    }

    @Override
    public void setState(String state) {
        setStartTest(Integer.parseInt(state));
    }

    @Override
    public void setStartTest(long state) {
        this.state = check(state);
    }

    @Override
    public void setEndTest(long end) {
        this.end = check(end);
    }

    @Override
    public void setOutput(Output output) {
        this.output = output;
    }

    @Override
    public Output getOutput() {
        return output;
    }

    @Override
    public long getTest() {
        return state;
    }

    @Override
    public boolean canFuzz() {
        return state <= end;
    }

    @Override
    public void moveOn() {
        if (state == Long.MAX_VALUE) {
            throw new IllegalStateException();
        }
        state++;
    }

    @Override
    public final Vector<Byte> fuzz(Vector<Byte> legacySessionId) {
        try {
            return generators[state].run(legacySessionId, output);
        } catch (IOException e) {
            // TODO: can we do better?
            throw new RuntimeException(e);
        }
    }

    private static int check(long state) {
        if (state < 0 || state > generators.length - 1) {
            throw new IllegalArgumentException(
                    String.format("state should be in [0, %d], but %d received",
                            generators.length - 1, state));
        }

        return (int) state;
    }

    private interface Generator {
        Vector<Byte> run (Vector<Byte> sessionId, Output output) throws IOException;
    }
}
