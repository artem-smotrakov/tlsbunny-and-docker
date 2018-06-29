package com.gypsyengineer.tlsbunny.utils;

import com.gypsyengineer.tlsbunny.tls.Struct;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Connection implements AutoCloseable {

    private static final long READ_DELAY = 100;
    public static final long DEFAULT_READ_TIMEOUT = 5000;  // in millis

    private final InputStream is;
    private final OutputStream os;
    private final long readTimeout;

    private Connection(InputStream is, OutputStream os, long readTimeout) {
        this.is = is;
        this.os = os;
        this.readTimeout = readTimeout;
    }

    public void send(ByteBuffer buffer) throws IOException {
        send(buffer.array());
    }

    public void send(byte[] data) throws IOException {
        os.write(data);
        os.flush();
    }

    public void send(Struct... objects) throws IOException {
        for (Struct object : objects) {
            send(object.encoding());
        }
    }

    public byte[] read() throws IOException {
        long start = System.currentTimeMillis();
        while (is.available() == 0) {
            Utils.sleep(READ_DELAY);

            if (System.currentTimeMillis() - start > readTimeout) {
                return new byte[0];
            }
        }

        byte[] bytes = new byte[is.available()];
        is.read(bytes);

        return bytes;
    }

    @Override
    public void close() throws IOException {
        is.close();
        os.close();
    }

    public static Connection create(String host, int port)
            throws IOException {
        return create(host, port, DEFAULT_READ_TIMEOUT);
    }

    public static Connection create(String host, int port, long readTimeout)
            throws IOException {

        if (readTimeout <= 0) {
            throw new IllegalArgumentException(String.format(
                    "what the hell? timeout should be more than 0, but %d passed",
                            readTimeout));
        }

        return create(new Socket(host, port), readTimeout);
    }

    public static Connection create(Socket socket, long readTimeout)
            throws IOException {

        return new Connection(
                new BufferedInputStream(socket.getInputStream()),
                new BufferedOutputStream(socket.getOutputStream()),
                readTimeout);
    }

}
