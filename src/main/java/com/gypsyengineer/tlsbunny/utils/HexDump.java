package com.gypsyengineer.tlsbunny.utils;

public class HexDump {

    public static String toHex(byte[] array) {
        return toHex(array, 0, array.length);
    }

    public static String toHex(byte[] array, int offset, int length) {
        final int width = 16;

        StringBuilder builder = new StringBuilder();

        for (int rowOffset = offset; rowOffset < offset + length; rowOffset += width) {
            builder.append(String.format("%06d:  ", rowOffset));

            for (int index = 0; index < width; index++) {
                if (rowOffset + index < array.length) {
                    builder.append(String.format("%02x ", array[rowOffset + index]));
                } else {
                    builder.append("   ");
                }
            }

            builder.append(String.format("%n"));
        }

        return builder.toString();
    }

}
