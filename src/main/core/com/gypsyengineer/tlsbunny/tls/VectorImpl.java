package com.gypsyengineer.tlsbunny.tls;

import com.gypsyengineer.tlsbunny.utils.Converter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VectorImpl<T> implements Vector<T> {

    private final int lengthBytes;
    private final List<T> objects;
    private final long maxEncodingLength;

    public VectorImpl(int lengthBytes) {
        this(lengthBytes, new ArrayList<>());
    }

    public VectorImpl(int lengthBytes, List<T> objects) {
        this.lengthBytes = lengthBytes;
        this.objects = objects;
        this.maxEncodingLength = Vector.maxEncodingLength(lengthBytes);
    }

    @Override
    public int size() {
        return objects.size();
    }

    @Override
    public boolean isEmpty() {
        return objects.isEmpty();
    }

    @Override
    public T get(int index) {
        return objects.get(index);
    }

    @Override
    public T first() {
        return objects.get(0);
    }

    @Override
    public void add(T object) {
        objects.add(object);
    }

    @Override
    public void set(int index, T object) {
        objects.set(index, object);
    }

    @Override
    public void clear() {
        objects.clear();
    }

    @Override
    public List<T> toList() {
        return objects;
    }

    @Override
    public int lengthBytes() {
        return lengthBytes;
    }

    @Override
    public byte[] bytes() throws IOException {
        List<byte[]> encodings = Vector.encodingsList(objects);
        ByteBuffer buffer = ByteBuffer.allocate(Vector.encodingsLength(encodings));

        for (byte[] encoding : encodings) {
            buffer.put(encoding);
        }

        return buffer.array();
    }

    @Override
    public int encodingLength() {
        if (objects.isEmpty()) {
            return lengthBytes;
        }

        int encodingLength = lengthBytes;
        for (T object : objects) {
            if (object instanceof Struct) {
                encodingLength += ((Struct) object).encodingLength();
            } else if (object instanceof Byte) {
                encodingLength++;
            } else {
                throw new IllegalArgumentException();
            }
        }

        return encodingLength;
    }

    @Override
    public byte[] encoding() throws IOException {
        if (objects.isEmpty()) {
            return new byte[lengthBytes];
        }

        List<byte[]> encodings = Vector.encodingsList(objects);
        int encodingLength = Vector.encodingsLength(encodings);

        if (encodingLength > maxEncodingLength) {
            throw new IllegalStateException(
                    String.format("encoding length is %d but max allowed is %d",
                            encodingLength, maxEncodingLength));
        }

        byte[] lengthEncoding = Converter.int2bytes(encodingLength, lengthBytes);
        ByteBuffer buffer = ByteBuffer.allocate(lengthBytes + encodingLength);
        buffer.put(lengthEncoding);

        for (byte[] encoding : encodings) {
            buffer.put(encoding);
        }

        return buffer.array();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        VectorImpl<?> vector = (VectorImpl<?>) o;
        return lengthBytes == vector.lengthBytes &&
                maxEncodingLength == vector.maxEncodingLength &&
                Objects.equals(objects, vector.objects);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lengthBytes, objects, maxEncodingLength);
    }
}
