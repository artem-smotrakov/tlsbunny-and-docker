package com.gypsyengineer.tlsbunny.fuzzer;

import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.utils.Converter;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FuzzedVector<T> implements Vector<T> {

    private final int lengthBytes;
    private final int length;
    private final byte[] content;

    public FuzzedVector(int lengthBytes, int length, byte[] content) {
        this.lengthBytes = lengthBytes;
        this.length = length;
        this.content = content;

        long maxEncodingLength = Vector.maxEncodingLength(lengthBytes);
        if (length > maxEncodingLength) {
            throw new IllegalArgumentException(
                    String.format("encoding length is %d but max allowed is %d",
                            length, maxEncodingLength));
        }
    }
    
    @Override
    public int size() {
        throw new UnsupportedOperationException("what the hell? I can't do that!");
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException("what the hell? I can't do that!");
    }

    @Override
    public T get(int index) {
        throw new UnsupportedOperationException("what the hell? I can't do that!");
    }

    @Override
    public T first() {
        throw new UnsupportedOperationException("what the hell? I can't do that!");
    }

    @Override
    public void add(T object) {
        throw new UnsupportedOperationException("what the hell? I can't do that!");
    }

    @Override
    public void set(int index, T object) {
        throw new UnsupportedOperationException("what the hell? I can't do that!");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("what the hell? I can't do that!");
    }

    @Override
    public List<T> toList() {
        throw new UnsupportedOperationException("what the hell? I can't do that!");
    }

    @Override
    public int lengthBytes() {
        return lengthBytes;
    }

    @Override
    public byte[] bytes() {
        return content;
    }

    @Override
    public int encodingLength() {
        return lengthBytes + content.length;
    }

    @Override
    public byte[] encoding() {
        return ByteBuffer.allocate(lengthBytes + content.length)
                .put(Converter.int2bytes(length, lengthBytes))
                .put(content)
                .array();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FuzzedVector<?> that = (FuzzedVector<?>) o;
        return lengthBytes == that.lengthBytes &&
                length == that.length &&
                Arrays.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(lengthBytes, length);
        result = 31 * result + Arrays.hashCode(content);
        return result;
    }
}