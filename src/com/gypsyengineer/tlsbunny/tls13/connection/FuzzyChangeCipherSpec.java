package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.fuzzer.Fuzzer;
import com.gypsyengineer.tlsbunny.tls13.struct.ChangeCipherSpec;
import com.gypsyengineer.tlsbunny.tls13.struct.ContentType;
import com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;
import com.gypsyengineer.tlsbunny.tls13.utils.Helper;

public class FuzzyChangeCipherSpec extends AbstractAction implements Fuzzer<ChangeCipherSpec> {

    private static final ChangeCipherSpec NO_INPUT = null;
    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 255;

    int start = MIN_VALUE;
    int end = MAX_VALUE;

    @Override
    public String name() {
        return "fuzzy ChangeCipherSpec";
    }

    @Override
    public Action run() throws Exception {
        ChangeCipherSpec ccs = fuzz(NO_INPUT);
        TLSPlaintext[] tlsPlaintexts = factory.createTLSPlaintexts(
                ContentType.change_cipher_spec,
                ProtocolVersion.TLSv12,
                ccs.encoding());

        buffer = Helper.store(tlsPlaintexts);

        return this;
    }

    @Override
    public String getState() {
        return Long.toString(start);
    }

    @Override
    public void setState(String state) {
        long value = Long.parseLong(state);
        if (value < 0) {
            throw new IllegalArgumentException();
        }
        setStartTest(value);
    }

    @Override
    public void setStartTest(long start) {
        this.start = check(start);
    }

    @Override
    public void setEndTest(long end) {
        this.end = check(end);
    }

    @Override
    public long getTest() {
        return start;
    }

    @Override
    public boolean canFuzz() {
        return start <= end;
    }

    @Override
    public ChangeCipherSpec fuzz(ChangeCipherSpec object) {
        return factory.createChangeCipherSpec(start);
    }

    @Override
    public void moveOn() {
        if (start == Long.MAX_VALUE) {
            throw new IllegalStateException();
        }
        start++;
    }

    private static int check(long value) {
        if (value < MIN_VALUE || value > MAX_VALUE) {
            throw new IllegalArgumentException();
        }

        return (int) value;
    }
}
