package com.gypsyengineer.tlsbunny.jsse;

import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.Output;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.*;

public class SimpleEchoServer implements Runnable, AutoCloseable {

    private static final int FREE_PORT = 0;
    private static final int delay = 500; // in millis

    protected Config config = SystemPropertiesConfig.load();
    protected Output output = new Output();

    // TODO: add synchronization
    private boolean running = false;
    private int maxConnections = 1;
    private int connections = 0;

    private final SSLServerSocket sslServerSocket;

    private SimpleEchoServer(SSLServerSocket sslServerSocket) {
        this.sslServerSocket = sslServerSocket;
    }

    public SimpleEchoServer set(Config config) {
        this.config = config;
        return this;
    }

    public SimpleEchoServer set(Output output) {
        this.output = output;
        return this;
    }

    public int port() {
        return sslServerSocket.getLocalPort();
    }

    public SimpleEchoServer maxConnections(int n) {
        maxConnections = n;
        return this;
    }

    @Override
    public void close() {
        if (sslServerSocket != null && !sslServerSocket.isClosed()) {
            close();
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
                output.info("received %d bytes: %s", len, new String(data));
                os.write(data, 0, len);
                output.info("done");
            } catch (Exception e) {
                output.achtung("exception: ", e);
                break;
            }
        }

        running = false;
        output.info("server stopped");
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
        connections++;
        if (maxConnections < 0) {
            return true;
        }

        return connections <= maxConnections;
    }

    public static SimpleEchoServer create() throws IOException {
        return create(FREE_PORT);
    }

    public static SimpleEchoServer create(int port) throws IOException {
        SSLServerSocket socket = (SSLServerSocket)
                SSLServerSocketFactory.getDefault().createServerSocket(port);
        socket.setEnabledProtocols(new String[] { "TLSv1.3" } );
        socket.setEnabledCipherSuites(new String[] { "TLS_AES_128_GCM_SHA256" } );
        return new SimpleEchoServer(socket);
    }
}
