package com.gypsyengineer.tlsbunny.fuzzer;

import java.util.BitSet;

public class BitFlipFuzzer extends AbstractFlipFuzzer {

    public static BitFlipFuzzer newBitFlipFuzzer() {
        return new BitFlipFuzzer();
    }

    public BitFlipFuzzer() {
        super();
    }

    public BitFlipFuzzer(double minRatio, double maxRatio) {
        this(minRatio, maxRatio, FROM_THE_BEGINNING, NOT_SPECIFIED);
    }

    public BitFlipFuzzer(double minRatio, double maxRatio, int start, int end) {
        super(minRatio, maxRatio, start, end);
    }

    @Override
    byte[] fuzzImpl(byte[] array) {
        BitSet bits = BitSet.valueOf(array);
        double ratio = getRatio();
        int start = getStartIndex();
        int end = getEndIndex(array);
        int startBit = start * 8;
        int endBit = (end + 1 ) * 8;
        int n = (int) Math.ceil((endBit - startBit) * ratio);

        // make sure that we fuzz at least one bit
        if (n == 0) {
            n = 1;
        }

        int[] processed = new int[n];
        int i = 0;
        while (i < n) {
            int pos = startBit + random.nextInt(endBit - startBit);

            boolean found = false;
            for (int k = 0; k < n; k++) {
                if (processed[k] == pos) {
                    found = true;
                    break;
                }
            }

            if (found) {
                continue;
            }

            bits.flip(pos);
            i++;
        }

        return bits.toByteArray();
    }

}
