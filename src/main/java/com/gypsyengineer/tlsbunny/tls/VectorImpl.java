package com.gypsyengineer.tlsbunny.tls;

import com.gypsyengineer.tlsbunny.utils.Convertor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class VectorImpl<T> implements Vector<T> {

    private final int lengthBytes;
    private final List<T> objects;

    public VectorImpl(int lengthBytes) {
        this(lengthBytes, new ArrayList<>());
    }

    public VectorImpl(int lengthBytes, List<T> objects) {
        this.lengthBytes = lengthBytes;
        this.objects = objects;
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
    public byte[] bytes() throws IOException {
        int encodingsLength = 0;
        List<byte[]> encodings = new ArrayList<>();
        for (T value : objects) {
            byte[] encoding;
            if (value instanceof Struct) {
                encoding = ((Struct) value).encoding();
            } else if (value instanceof Byte) {
                encoding = new byte[] { (Byte) value };
            } else {
                throw new IllegalArgumentException();
            }

            encodings.add(encoding);
            encodingsLength += encoding.length;
        }

        ByteBuffer buffer = ByteBuffer.allocate(encodingsLength);
        encodings.forEach((encoding) -> {
            buffer.put(encoding);
        });

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

        int encodingsLength = 0;
        List<byte[]> encodings = new ArrayList<>();
        for (T value : objects) {
            byte[] encoding;
            if (value instanceof Struct) {
                encoding = ((Struct) value).encoding();
            } else if (value instanceof Byte) {
                encoding = new byte[] { (Byte) value };
            } else {
                throw new IllegalArgumentException();
            }

            encodings.add(encoding);
            encodingsLength += encoding.length;
        }

        long maxEncodingLength = (long) (Math.pow(256, lengthBytes) - 1);
        if (encodingsLength > maxEncodingLength) {
            throw new IllegalStateException(
                    String.format("encoding length is %d but max expected is %d",
                            encodingsLength, maxEncodingLength));
        }

        byte[] lengthEncoding = Convertor.int2bytes(encodingsLength, lengthBytes);
        ByteBuffer buffer = ByteBuffer.allocate(lengthBytes + encodingsLength);
        buffer.put(lengthEncoding);
        encodings.forEach((encoding) -> {
            buffer.put(encoding);
        });

        return buffer.array();
    }
}
