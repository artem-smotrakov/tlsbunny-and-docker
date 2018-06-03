package com.gypsyengineer.tlsbunny.tls13.fuzzer;

public class ByteFlipFuzzer extends AbstractFlipFuzzer implements Fuzzer<byte[]> {

    public static ByteFlipFuzzer newByteFlipFuzzer() {
        return new ByteFlipFuzzer();
    }

    public ByteFlipFuzzer() {
        super();
    }

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

        int[] processed = new int[n];
        int i = 0;
        while (i < n) {
            int pos = start + random.nextInt(end - start + 1);

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

            fuzzed[pos] = (byte) random.nextInt(256);
            i++;
        }

        return fuzzed;
    }

}
