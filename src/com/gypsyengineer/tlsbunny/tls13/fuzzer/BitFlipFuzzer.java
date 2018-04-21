package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import java.util.BitSet;

public class BitFlipFuzzer extends AbstractFlipFuzzer {

    public BitFlipFuzzer(double minRatio, double maxRatio) {
        this(minRatio, maxRatio, FROM_THE_BEGINNING, NOT_SPECIFIED);
    }

    public BitFlipFuzzer(double minRatio, double maxRatio, int start, int end) {
        super(minRatio, maxRatio, start, end);
    }

    @Override
    protected byte[] fuzzImpl(byte[] array) {
        BitSet bits = BitSet.valueOf(array);
        double ratio = getRatio();
        int start = getStartIndex();
        int end = getEndIndex(array);
        int startBit = start * 8;
        int endBit = end * 8;
        int n = (int) Math.ceil((endBit - startBit) * ratio);

        // make sure that we fuzz at least one bit
        if (n == 0) {
            n = 1;
        }

        for (int i = 0; i < n; i++) {
            int pos = startBit + random.nextInt(endBit - startBit);
            bits.flip(pos);
        }

        return bits.toByteArray();
    }

}
