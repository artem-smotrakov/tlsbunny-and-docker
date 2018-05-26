package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.utils.Convertor;

import java.nio.ByteBuffer;
import java.util.List;

public class FuzzedVector<T> implements Vector<T> {

    private final int lengthBytes;
    private final int encodingLength;
    private final byte[] bytes;

    public FuzzedVector(int lengthBytes, int encodingLength, byte[] bytes) {
        this.lengthBytes = lengthBytes;
        this.encodingLength = encodingLength;
        this.bytes = bytes;

        long maxEncodingLength = Vector.maxEncodingLength(lengthBytes);
        if (encodingLength > maxEncodingLength) {
            throw new IllegalStateException(
                    String.format("encoding length is %d but max allowed is %d",
                            encodingLength, maxEncodingLength));
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
    public byte[] bytes() {
        return bytes;
    }

    @Override
    public int encodingLength() {
        return encodingLength;
    }

    @Override
    public byte[] encoding() {
        return ByteBuffer.allocate(lengthBytes + encodingLength)
                .put(Convertor.int2bytes(encodingLength, lengthBytes))
                .put(bytes)
                .array();
    }

}
