package com.gypsyengineer.tlsbunny.tls13.fuzzer;

public class ByteFlipFuzzer extends AbstractFlipFuzzer implements Fuzzer<byte[]> {

    public ByteFlipFuzzer(
            double minRatio, double maxRatio, int startIndex, int endIndex) {

        super(minRatio, maxRatio, startIndex, endIndex);
    }

    @Override
    public byte[] fuzz(byte[] array) {
        byte[] fuzzed = array.clone();
        double ratio = getRatio();
        int n = (int) Math.ceil((endIndex - startIndex) * ratio);
        for (int i = 0; i < n; i++) {
            int pos = startIndex + random.nextInt(endIndex - startIndex);
            fuzzed[pos] = (byte) random.nextInt(256);
        }

        return fuzzed;
    }

}
