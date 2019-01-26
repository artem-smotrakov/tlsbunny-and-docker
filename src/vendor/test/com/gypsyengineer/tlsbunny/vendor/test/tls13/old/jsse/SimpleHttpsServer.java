package com.gypsyengineer.tlsbunny.vendor.test.tls13.old.jsse;

import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.Output;
import com.gypsyengineer.tlsbunny.utils.Output;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.*;

public class SimpleHttpsServer implements Runnable, AutoCloseable {

    private static final int free = 0;
    private static final int delay = 500; // in millis
    private static final String message =
            "<html>Like most of life's problems, this one can be solved with bending.</html>";
    private static final byte[] response = String.format(
            "HTTP/1.1 200 OK\n" +
            "Server: JSSE\n" +
            "Content-Length: %d\n" +
            "Content-Type: text/html\n" +
            "Connection: Closed\n\n%s", message.length(), message).getBytes();

    protected Config config = SystemPropertiesConfig.load();
    protected Output output = Output.console();

    // TODO: add synchronization
    private boolean running = false;
    private int maxConnections = 1;
    private int connections = 0;

    private final SSLServerSocket sslServerSocket;

    private SimpleHttpsServer(SSLServerSocket sslServerSocket) {
        this.sslServerSocket = sslServerSocket;
    }

    public SimpleHttpsServer set(Config config) {
        this.config = config;
        return this;
    }

    public SimpleHttpsServer set(Output output) {
        this.output = output;
        return this;
    }

    public int port() {
        return sslServerSocket.getLocalPort();
    }

    public SimpleHttpsServer maxConnections(int n) {
        maxConnections = n;
        return this;
    }

    @Override
    public void close() throws IOException {
        if (sslServerSocket != null && !sslServerSocket.isClosed()) {
            sslServerSocket.close();
        }

        if (output != null) {
            output.flush();
        }
    }

    @Override
    public void run() {
        output.info("server started on port %d", port());

        running = true;
        while (shouldRun()) {
            try (SSLSocket socket = (SSLSocket) sslServerSocket.accept()) {
                output.info("accepted");
                InputStream is = new BufferedInputStream(socket.getInputStream());
                OutputStream os = new BufferedOutputStream(socket.getOutputStream());
                byte[] data = new byte[2048];
                int len = is.read(data);
                output.info("received %d bytes: %s", len, new String(data, 0, len));
                os.write(response);
                os.flush();
                output.info("done");
            } catch (Exception e) {
                output.achtung("exception: ", e);
                break;
            }
        }

        running = false;
        output.info("server stopped");
    }

    public void stop() {
        try {
            sslServerSocket.close();
        } catch (IOException e) {
            output.info("exception occurred while closing server socket:", e);
        }
    }

    public void await() {
        while (running) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                output.achtung("exception occurred while waiting", e);
            }
        }
    }

    private boolean shouldRun() {
        if (sslServerSocket.isClosed()) {
            return false;
        }

        connections++;
        if (maxConnections < 0) {
            return true;
        }

        return connections <= maxConnections;
    }

    public static SimpleHttpsServer create() throws IOException {
        return create(free);
    }

    public static SimpleHttpsServer create(int port) throws IOException {
        SSLServerSocket socket = (SSLServerSocket)
                SSLServerSocketFactory.getDefault().createServerSocket(port);
        socket.setEnabledProtocols(new String[] { "TLSv1.3" } );
        socket.setEnabledCipherSuites(new String[] { "TLS_AES_128_GCM_SHA256" } );
        return new SimpleHttpsServer(socket);
    }
}
