package com.gypsyengineer.tlsbunny.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import com.gypsyengineer.tlsbunny.tls.Entity;

public class Connection implements AutoCloseable {

    private static final long READ_DELAY = 100;
    private static final long READ_TIMEOUT = 5000;

    private final Socket socket;
    private final InputStream is;
    private final OutputStream os;

    private Connection(Socket socket, InputStream is, OutputStream os) {
        this.socket = socket;
        this.is = is;
        this.os = os;
    }

    public void send(byte[] data) throws IOException {
        os.write(data);
        os.flush();
    }

    public void send(Entity... objects) throws IOException {       
        for (Entity object : objects) {
            send(object.encoding());
        }
    }

    public byte[] read() throws IOException {
        long start = System.currentTimeMillis();
        while (is.available() == 0) {
            Utils.sleep(READ_DELAY);

            if (System.currentTimeMillis() - start > READ_TIMEOUT) {
                return new byte[0];
            }
        }

        byte[] bytes = new byte[is.available()];
        is.read(bytes);
        
        return bytes;
    }

    @Override
    public void close() throws IOException {
        if (!socket.isClosed()) {
            socket.close();
        }
    }
    
    public static Connection create(String host, int port) throws IOException {
        Socket socket = new Socket(host, port);
        InputStream is = new BufferedInputStream(socket.getInputStream());
        OutputStream os = new BufferedOutputStream(socket.getOutputStream());
        
        return new Connection(socket, is, os);
    }

}
