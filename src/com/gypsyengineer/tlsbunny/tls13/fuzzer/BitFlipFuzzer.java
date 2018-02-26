package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import java.util.BitSet;

public class BitFlipFuzzer extends AbstractFlipFuzzer {

    public BitFlipFuzzer(
            double minRatio, double maxRatio, int startIndex, int endIndex) {

        super(minRatio, maxRatio, startIndex, endIndex);
    }

    @Override
    public byte[] fuzz(byte[] array) {
        BitSet bits = BitSet.valueOf(array);
        double ratio = getRatio();
        int startBit = startIndex * 8;
        int endBit = endIndex * 8;
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
