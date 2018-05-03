package com.gypsyengineer.tlsbunny.utils;

public class HexDump {

    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

    public static String toHex(byte[] array) {
        return toHex(array, 0, array.length);
    }

    public static String toHex(byte[] array, int offset, int length) {
        final int width = 16;

        StringBuilder builder = new StringBuilder();

        for (int rowOffset = offset; rowOffset < offset + length; rowOffset += width) {
            builder.append(String.format("%04x:  ", rowOffset));

            for (int index = 0; index < width; index++) {
                int k = rowOffset + index;
                if (k < array.length) {
                    builder.append(String.format("%02x ", array[k]));
                } else {
                    builder.append("   ");
                }
            }

            builder.append(String.format("%n"));
        }

        return builder.toString();
    }

    public static String toHexDiff(byte[] array, byte[] original) {
        return toHexDiff(array, original, 0, array.length);
    }

    public static String toHexDiff(byte[] array, byte[] original, int offset, int length) {
        if (array.length != original.length) {
            // TODO: that doesn't seem to be necessary
            throw new IllegalArgumentException("what the hell? expected arrays of the same length!");
        }

        final int width = 16;

        StringBuilder builder = new StringBuilder();

        for (int rowOffset = offset; rowOffset < offset + length; rowOffset += width) {
            builder.append(String.format("%04x:  ", rowOffset));

            for (int index = 0; index < width; index++) {
                int k = rowOffset + index;
                if (k < array.length) {
                    if (array[k] == original[k]) {
                        builder.append(String.format("%02x ", array[k]));
                    } else {
                        builder.append(String.format("%s%02x%s ", ANSI_RED, array[k], ANSI_RESET));
                    }
                } else {
                    builder.append("   ");
                }
            }

            builder.append(String.format("%n"));
        }

        return builder.toString();
    }

}
