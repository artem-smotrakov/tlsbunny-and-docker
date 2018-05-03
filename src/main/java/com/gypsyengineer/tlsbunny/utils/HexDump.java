package com.gypsyengineer.tlsbunny.utils;

public class HexDump {

    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final int WIDTH = 16;

    public static String printHex(byte[] array) {
        return printHex(array, 0, array.length);
    }

    public static String printHex(byte[] array, int offset, int length) {
        StringBuilder builder = new StringBuilder();

        for (int rowOffset = offset; rowOffset < offset + length; rowOffset += WIDTH) {
            builder.append(String.format("%04x:  ", rowOffset));

            for (int index = 0; index < WIDTH; index++) {
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

    public static String printHexDiff(byte[] array, byte[] original) {
        return printHexDiff(array, original, 0, array.length);
    }

    public static String printHexDiff(byte[] array, byte[] original, int offset, int length) {
        StringBuilder builder = new StringBuilder();

        for (int rowOffset = offset; rowOffset < offset + length; rowOffset += WIDTH) {
            builder.append(String.format("%04x:  ", rowOffset));

            for (int index = 0; index < WIDTH; index++) {
                int k = rowOffset + index;
                if (k < array.length) {
                    if (k >= original.length || array[k] != original[k]) {
                        builder.append(String.format("%s%02x%s ", ANSI_RED, array[k], ANSI_RESET));
                    } else {
                        builder.append(String.format("%02x ", array[k]));
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
