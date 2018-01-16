package com.gypsyengineer.tlsbunny.tls;

import com.gypsyengineer.tlsbunny.utils.Convertor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Vector<T> implements Entity {

    public static interface ContentParser<T> {
        T parse(ByteBuffer buffer);
    }

    private final int lengthBytes;
    private final List<T> objects;

    public Vector(int lengthBytes) {
        this(lengthBytes, new ArrayList<>());
    }

    public Vector(int lengthBytes, List<T> objects) {
        this.lengthBytes = lengthBytes;
        this.objects = objects;
    }

    public int size() {
        return objects.size();
    }
    public boolean isEmpty() {
        return objects.isEmpty();
    }

    public T get(int index) {
        return objects.get(index);
    }

    public T first() {
        return objects.get(0);
    }

    public void add(T object) {
        objects.add(object);
    }

    public void set(int index, T object) {
        objects.set(index, object);
    }

    public void clear() {
        objects.clear();
    }

    public List<T> toList() {
        return objects;
    }

    @Override
    public int encodingLength() {
        if (objects.isEmpty()) {
            return lengthBytes;
        }

        int encodingLength = lengthBytes;
        for (T object : objects) {
            if (object instanceof Entity) {
                encodingLength += ((Entity) object).encodingLength();
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
            if (value instanceof Entity) {
                encoding = ((Entity) value).encoding();
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
            throw new IllegalStateException();
        }

        byte[] lengthEncoding = Convertor.int2bytes(encodingsLength, lengthBytes);
        ByteBuffer buffer = ByteBuffer.allocate(lengthBytes + encodingsLength);
        buffer.put(lengthEncoding);
        encodings.forEach((encoding) -> {
            buffer.put(encoding);
        });

        return buffer.array();
    }

    public static <T> Vector<T> parse(ByteBuffer buffer, int lengthBytes,
            ContentParser<T> parser) {

        List<T> objects = new ArrayList<>();
        buffer = ByteBuffer.wrap(parseOpaque(buffer, lengthBytes));
        while (buffer.remaining() > 0) {
            objects.add(parser.parse(buffer));
        }

        return new Vector<>(lengthBytes, objects);
    }

    public static byte[] parseOpaque(ByteBuffer buffer, int lengthBytes) {
        byte[] encodedLength = new byte[lengthBytes];
        buffer.get(encodedLength);
        int length = Convertor.bytes2int(encodedLength);
        byte[] bytes = new byte[length];
        buffer.get(bytes);

        return bytes;
    }

    public static <T> Vector<T> create(int maxLength) {
        return new Vector<>(lengthBytes(maxLength));
    }

    public static <T> Vector<T> wrap(int maxLength, List<T> objects) {
        return new Vector<>(lengthBytes(maxLength), objects);
    }

    public static <T> Vector<T> wrap(int maxLength, T... objects) {
        List<T> objectList = new ArrayList<>();
        objectList.addAll(Arrays.asList(objects));
        return wrap(maxLength, objectList);
    }

    public static Vector<Byte> wrap(int maxLength, byte[] bytes) {
        return new Vector<>(lengthBytes(maxLength), toList(bytes));
    }

    private static List<Byte> toList(byte[] bytes) {
        List<Byte> objects = new ArrayList<>();
        for (byte b : bytes) {
            objects.add(b);
        }

        return objects;
    }

    private static int lengthBytes(int length) {
        if (length < 256) {
            return 1;
        }
        
        if (length < 65536) {
            return 2;
        }
        
        if (length < 16777216) {
            return 3;
        }
        
        throw new IllegalArgumentException();
    }

}
