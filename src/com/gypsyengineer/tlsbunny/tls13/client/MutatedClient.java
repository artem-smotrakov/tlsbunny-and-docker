package com.gypsyengineer.tlsbunny.tls13.client;

import com.gypsyengineer.tlsbunny.tls13.crypto.ApplicationDataChannel;
import com.gypsyengineer.tlsbunny.tls13.fuzzer.MutatedStructFactory;
import com.gypsyengineer.tlsbunny.tls13.handshake.ClientHandshaker;
import com.gypsyengineer.tlsbunny.tls13.handshake.ECDHENegotiator;
import com.gypsyengineer.tlsbunny.tls13.struct.CipherSuite;
import com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup;
import com.gypsyengineer.tlsbunny.tls13.struct.SignatureScheme;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.utils.CertificateHolder;
import com.gypsyengineer.tlsbunny.utils.Connection;
import com.gypsyengineer.tlsbunny.utils.Output;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

/**
 * This is a fuzzy TLS client which wraps a MutatedStructFactory instance.
 * MutatedClient starts handshaking where it uses mutated messages produced by MutatedStructFactory.
 */
public class MutatedClient implements Runnable {

    private static final byte[] HTTP_GET_REQUEST = "GET / HTTP/1.1\n\n".getBytes();

    private final MutatedStructFactory fuzzer;
    private final Output output;
    private final String host;
    private final int port;
    private final int total;

    public MutatedClient(MutatedStructFactory fuzzer, Output output,
            String host, int port, int total) {

        this.fuzzer = fuzzer;
        this.output = output;
        this.host = host;
        this.port = port;
        this.total = total;
    }

    @Override
    public void run() {
        try {
            ClientHandshaker handshaker = ClientHandshaker.create(
                    fuzzer,
                    SignatureScheme.ecdsa_secp256r1_sha256,
                    NamedGroup.secp256r1,
                    ECDHENegotiator.create(NamedGroup.secp256r1, fuzzer),
                    CipherSuite.TLS_AES_128_GCM_SHA256,
                    CertificateHolder.NO_CERTIFICATE);

            int test = 0;
            String threadName = Thread.currentThread().getName();
            while (fuzzer.canFuzz() && test < total) {
                output.info("%s, test %d of %d", threadName, test, total);
                output.info("now fuzzer's state is '%s'", fuzzer.getState());
                try (Connection connection = Connection.create(host, port)) {
                    output.info("start handshaking");
                    ApplicationDataChannel applicationData = handshaker.start(connection);

                    if (handshaker.receivedAlert()) {
                        output.info("received %s", handshaker.getReceivedAlert());
                        continue;
                    }

                    if (applicationData == null) {
                        // TODO: check what happened
                        output.info("handshake failed, "
                                + "couldn't create an application data channel");
                        continue;
                    }

                    sendApplicationData(applicationData, output);
                    if (!applicationData.isAlive()) {
                        output.info("connection closed");
                        continue;
                    }
                    output.info("send application data again");
                    sendApplicationData(applicationData, output);
                    output.achtung("holy cow, no alert!");
                } finally {
                    output.flush();
                    test++;
                    fuzzer.moveOn();
                }
            }
        } catch (IOException e) {
            output.info("looks like the server closed connection", e);
        } catch (Exception e) {
            output.achtung("what the hell? unexpected exception", e);
        } finally {
            output.flush();
        }
    }

    public static void runFuzzer(ExecutorService executor,
            String host, int port,
            String target, String mode,
            double minRatio, double maxRatio,
            int test, int total) {

        Output output = new Output();

        MutatedStructFactory fuzzer = new MutatedStructFactory(
                        StructFactory.getDefault(), output, minRatio, maxRatio);

        fuzzer.setTarget(target);
        if (!mode.isEmpty()) {
            fuzzer.setMode(mode);
        }

        fuzzer.setTest(test);

        executor.submit(new MutatedClient(fuzzer, output, host, port, total));
    }

    private static void sendApplicationData(
            ApplicationDataChannel applicationData, Output output) throws Exception {

        applicationData.send(HTTP_GET_REQUEST);
        byte[] bytes = applicationData.receive();
        if (bytes.length > 0) {
            output.info("we just received %d bytes:%n%s", bytes.length, new String(bytes));
        } else {
            output.info("0 bytes received");
        }

        output.info("holy moly, no alert received so far!");
    }
 }
