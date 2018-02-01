package com.gypsyengineer.tlsbunny.utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.gypsyengineer.tlsbunny.tls.Struct;

public class Utils {
    
    public static final byte[] EMPTY_ARRAY = new byte[0];
    public static final String PREFIX = "[tlsbunny]";
    
    public static byte[][] split(byte[] data, int length) {
        List<byte[]> fragments = new ArrayList<>();
        if (data.length <= length) {
            fragments.add(data);
            return fragments.toArray(new byte[1][]);
        }

        int i = 0;
        while (i < data.length - length) {
            fragments.add(Arrays.copyOfRange(data, i, i + length));
            i += length;
        }

        if (i < data.length) {
            fragments.add(Arrays.copyOfRange(data, i, data.length));
        }

        return fragments.toArray(new byte[fragments.size()][]);
    }

    public static byte[] concatenate(byte[]... arrays) {
        int total = 0;
        for (byte[] array : arrays) {
            total += array.length;
        }

        ByteBuffer buffer = ByteBuffer.allocate(total);
        for (byte[] array : arrays) {
            buffer.put(array);
        }

        return buffer.array();
    }

    public static byte[] xor(byte[] bytes1, byte[] bytes2) {
        if (bytes1.length != bytes2.length) {
            throw new IllegalArgumentException();
        }

        byte[] result = new byte[bytes1.length];
        for (int i = 0; i < bytes1.length; i++) {
            result[i] = (byte) (bytes1[i] ^ bytes2[i]);
        }

        return result;
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getEncodingLength(Struct... objects) {
        return Arrays.stream(objects)
                .map(object -> object.encodingLength())
                .reduce(0, Integer::sum);
    }

    public static byte[] encoding(Struct... objects) throws IOException {
        List<byte[]> encodings = new ArrayList<>(objects.length);

        int total = 0;
        for (Struct object : objects) {
            byte[] encoding = object.encoding();
            encodings.add(encoding);
            total += encoding.length;
        }

        ByteBuffer buffer = ByteBuffer.allocate(total);
        encodings.forEach(endoding -> buffer.put(endoding));

        return buffer.array();
    }
    
    public static void info(String format, Object... values) {
        System.out.printf("%s %s%n", PREFIX, String.format(format, values));
    }
    
    public static void achtung(String format, Object... values) {
        System.out.printf("%s achtung: %s%n", PREFIX, String.format(format, values));
    }

}
