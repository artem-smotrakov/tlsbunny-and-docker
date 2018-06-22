package com.gypsyengineer.tlsbunny.tls13.connection.action.composite;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.ActionFailed;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Phase;
import com.gypsyengineer.tlsbunny.tls13.connection.action.simple.*;
import com.gypsyengineer.tlsbunny.tls13.crypto.AEADException;
import com.gypsyengineer.tlsbunny.tls13.handshake.NegotiatorException;
import com.gypsyengineer.tlsbunny.tls13.struct.*;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ProcessingMessages extends AbstractAction<ProcessingMessages> {

    @Override
    public String name() {
        return "processing messages";
    }

    @Override
    public ProcessingMessages run() throws AEADException, ActionFailed,
            IOException, NegotiatorException {

        while (in.remaining() > 0) {
            TLSPlaintext tlsPlaintext = new ProcessingTLSPlaintext()
                    .set(output).set(context).in(in).run().tlsPlaintext();

            if (tlsPlaintext.containsHandshake()) {
                processHandshake(tlsPlaintext);
                continue;
            }

            if (tlsPlaintext.containsChangeCipherSpec()) {
                processChangeCipherSpec(tlsPlaintext);
                continue;
            }

            if (tlsPlaintext.containsAlert()) {
                processAlert(tlsPlaintext);
                continue;
            }

            if (tlsPlaintext.containsApplicationData()) {
                processApplicationData(tlsPlaintext);
                continue;
            }

            throw new IllegalStateException("what the hell? unexpected content type!");
        }

        return this;
    }

    private void processHandshake(TLSPlaintext tlsPlaintext)
            throws ActionFailed, NegotiatorException, IOException, AEADException {

        ByteBuffer buffer = ByteBuffer.wrap(tlsPlaintext.getFragment());
        while (buffer.remaining() > 0) {
            Handshake handshake = new ProcessingHandshake()
                    .set(output).set(context).run().handshake();

            if (handshake.containsClientHello()) {
                processClientHello(handshake);
                continue;
            }

            if (handshake.containsServerHello()) {
                processServerHello(handshake);
                continue;
            }

            if (handshake.containsHelloRetryRequest()) {
                processHelloRetryRequest(handshake);
                continue;
            }

            if (handshake.containsEncryptedExtensions()) {
                processEncryptedExtensions(handshake);
                continue;
            }

            if (handshake.containsCertificate()) {
                processCertificate(handshake);
                continue;
            }

            if (handshake.containsCertificateVerify()) {
                processCertificateVerify(handshake);
                continue;
            }

            if (handshake.containsFinished()) {
                processFinished(handshake);
                continue;
            }

            if (handshake.containsNewSessionTicket()) {
                processNewSessionTicket(handshake);
                continue;
            }

            throw new IllegalStateException("what the hell? unexpected handshake message!");
        }
    }

    private void processChangeCipherSpec(TLSPlaintext tlsPlaintext) throws ActionFailed {
        new ProcessingChangeCipherSpec().set(output).set(context)
                .in(ByteBuffer.wrap(tlsPlaintext.getFragment())).run();
    }

    private void processAlert(TLSPlaintext tlsPlaintext) {
        new ProcessingAlert().set(output).set(context)
                .in(ByteBuffer.wrap(tlsPlaintext.getFragment())).run();
    }

    private void processApplicationData(TLSPlaintext tlsCiphertext)
            throws AEADException, IOException, ActionFailed {

        if (context.hasServerFinished()) {
            new ProcessingTLSCiphertext(Phase.application_data)
                    .set(output).set(context).set(tlsCiphertext).run();
        } else {
            new ProcessingTLSCiphertext(Phase.handshake)
                    .set(output).set(context).set(tlsCiphertext).run();
        }
    }

    private void processClientHello(Handshake handshake) {
        throw new UnsupportedOperationException("no message processing for you!");
    }

    private void processServerHello(Handshake handshake)
            throws IOException, AEADException, NegotiatorException {

        new ProcessingServerHello().set(output).set(context)
                .in(ByteBuffer.wrap(handshake.getBody())).run();

        context.setServerHello(handshake);

        new NegotiatingDHSecret().set(output).set(context).run();
        new ComputingKeysAfterServerHello().set(output).set(context).run();
    }

    private void processHelloRetryRequest(Handshake handshake) {
        throw new UnsupportedOperationException("no message processing for you!");
    }

    private void processEncryptedExtensions(Handshake handshake) {
        new ProcessingEncryptedExtensions().set(output).set(context)
                .in(ByteBuffer.wrap(handshake.getBody())).run();

        context.setEncryptedExtensions(handshake);
    }

    private void processCertificate(Handshake handshake) {
        new ProcessingCertificate().set(output).set(context)
                .in(ByteBuffer.wrap(handshake.getBody())).run();

        context.setServerCertificate(handshake);
    }

    private void processCertificateVerify(Handshake handshake) {
        new ProcessingCertificateVerify().set(output).set(context)
                .in(ByteBuffer.wrap(handshake.getBody())).run();

        context.setServerCertificateVerify(handshake);
    }

    private void processFinished(Handshake handshake)
            throws IOException, ActionFailed {

        new ProcessingFinished().set(output).set(context)
                .in(ByteBuffer.wrap(handshake.getBody())).run();

        context.setServerFinished(handshake);

        new ComputingKeysAfterServerFinished().set(output).set(context).run();
    }

    private void processNewSessionTicket(Handshake handshake) throws IOException {
        new ProcessingNewSessionTicket().set(output).set(context)
                .in(ByteBuffer.wrap(handshake.getBody())).run();
    }

}
