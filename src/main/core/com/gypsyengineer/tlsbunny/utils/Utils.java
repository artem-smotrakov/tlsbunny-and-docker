package com.gypsyengineer.tlsbunny.utils;

import com.gypsyengineer.tlsbunny.tls.Struct;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public class Utils {

    public static final long DEFAULT_SEED = 0;
    public static final long SEED = Long.getLong("tlsbunny.seed", DEFAULT_SEED);
    public static final byte[] empty_array = new byte[0];

    public static List<Byte> toList(byte[] bytes) {
        List<Byte> objects = new ArrayList<>();
        for (byte b : bytes) {
            objects.add(b);
        }

        return objects;
    }

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

    public static byte[] concatenate(List<byte[]> arrays) {
        return concatenate(arrays.toArray(new byte[arrays.size()][]));
    }

    public static byte[] xor(byte[] bytes1, byte[] bytes2) {
        if (bytes1.length != bytes2.length) {
            throw whatTheHell("array length are not equal!");
        }

        byte[] result = new byte[bytes1.length];
        for (int i = 0; i < bytes1.length; i++) {
            result[i] = (byte) (bytes1[i] ^ bytes2[i]);
        }

        return result;
    }

    public static byte[] zeroes(int length) {
        return new byte[length];
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw whatTheHell("could not sleep well!", e);
        }
    }

    // returns true if the integer is found in the array
    public static boolean contains(int i, int... array) {
        if (array == null || array.length == 0) {
            return false;
        }

        for (int element : array) {
            if (element == i) {
                return true;
            }
        }

        return false;
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
        encodings.forEach(encoding -> buffer.put(encoding));

        return buffer.array();
    }

    public static <T> T cast(Struct object, Class<T> clazz) {
        if (!clazz.isAssignableFrom(object.getClass())) {
            throw whatTheHell("expected %s but received %s",
                    clazz.getSimpleName(), object.getClass().getSimpleName());
        }
        return (T) object;
    }

    public static boolean lastBytesEquals(byte[] bytes, byte[] message) {
        int i = bytes.length - message.length;
        int j = 0;
        while (j < message.length) {
            if (bytes[i++] != message[j++]) {
                return false;
            }
        }

        return true;
    }

    public static String toString(Throwable e) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(baos, true));
        return new String(baos.toByteArray());
    }
}
