package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;
import com.gypsyengineer.tlsbunny.tls13.struct.ChangeCipherSpec;
import com.gypsyengineer.tlsbunny.tls13.struct.ContentType;
import com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;
import com.gypsyengineer.tlsbunny.tls13.utils.Helper;

public class FuzzyChangeCipherSpec extends AbstractAction implements Fuzzer<ChangeCipherSpec> {

    public static final String STATE_DELIMITER = ":";
    public static final String STATE_PREFIX = "fuzzy_ccs";

    private static final ChangeCipherSpec NO_INPUT = null;
    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 255;

    int value = MIN_VALUE;
    int end = MAX_VALUE;

    @Override
    public String name() {
        return "fuzzy ChangeCipherSpec";
    }

    @Override
    public Action run() throws Exception {
        ChangeCipherSpec ccs = fuzz(NO_INPUT);
        TLSPlaintext[] tlsPlaintexts = context.factory.createTLSPlaintexts(
                ContentType.change_cipher_spec,
                ProtocolVersion.TLSv12,
                ccs.encoding());

        in = Helper.store(tlsPlaintexts);

        return this;
    }

    @Override
    public String getState() {
        return String.format("%s%s%d", STATE_PREFIX, STATE_DELIMITER, value);
    }

    @Override
    public void setState(String state) {
        if (state == null || state.isEmpty()) {
            throw new IllegalArgumentException();
        }

        String[] parts = state.trim().split(STATE_DELIMITER);
        if (parts.length != 2) {
            throw new IllegalArgumentException();
        }

        if (!STATE_PREFIX.equals(parts[0])) {
            throw new IllegalArgumentException();
        }

        setStartTest(Long.parseLong(parts[1]));
    }

    @Override
    public void setStartTest(long start) {
        this.value = check(start);
    }

    @Override
    public void setEndTest(long end) {
        this.end = check(end);
    }

    @Override
    public long getTest() {
        return value;
    }

    @Override
    public boolean canFuzz() {
        return value <= end;
    }

    @Override
    public ChangeCipherSpec fuzz(ChangeCipherSpec object) {
        return context.factory.createChangeCipherSpec(value);
    }

    @Override
    public void moveOn() {
        value++;
    }

    private static int check(long value) {
        if (value < MIN_VALUE || value > MAX_VALUE) {
            throw new IllegalArgumentException();
        }

        return (int) value;
    }
}
