package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import com.gypsyengineer.tlsbunny.tls.Vector;

import java.io.IOException;

public class LegacySessionIdFuzzer implements Fuzzer<Vector<Byte>> {

    public static final int LENGTH_BYTES = 1;

    public static class FuzzedLegacySessionId extends FuzzedVector<Byte> {

        public FuzzedLegacySessionId(int encodingLength, byte[] bytes) {
            super(LENGTH_BYTES, encodingLength, bytes);
        }
    }

    private static final Generator[] generators = {
        legacySessionId -> new FuzzedLegacySessionId(0, legacySessionId.bytes()),
        legacySessionId -> new FuzzedLegacySessionId(1,  legacySessionId.bytes()),
        legacySessionId -> new FuzzedLegacySessionId(
                legacySessionId.bytes().length + 1,
                legacySessionId.bytes())
    };

    private int state = 0;
    private int end = generators.length - 1;

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
            return generators[state].run(legacySessionId);
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
        Vector<Byte> run (Vector<Byte> legacySessionId) throws IOException;
    }
}
