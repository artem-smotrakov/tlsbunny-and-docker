package com.gypsyengineer.tlsbunny.tls13.fuzzer;

public class ByteFlipFuzzer extends AbstractFlipFuzzer implements Fuzzer<byte[]> {

    public ByteFlipFuzzer(double minRatio, double maxRatio, int start, int end) {
        super(minRatio, maxRatio, start, end);
    }

    @Override
    protected byte[] fuzzImpl(byte[] array) {
        byte[] fuzzed = array.clone();
        double ratio = getRatio();
        int start = getStartIndex();
        int end = getEndIndex(array);
        int n = (int) Math.ceil((end - start) * ratio);

        // make sure what we fuzz at least one byte
        if (n == 0) {
            n = 1;
        }

        for (int i = 0; i < n; i++) {
            int pos = start + random.nextInt(end - start);
            fuzzed[pos] = (byte) random.nextInt(256);
        }

        return fuzzed;
    }

}
