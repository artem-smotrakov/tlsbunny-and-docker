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

public class IncomingMessages extends AbstractAction<IncomingMessages> {

    @Override
    public String name() {
        return "incoming messages";
    }

    @Override
    public IncomingMessages run() throws AEADException, ActionFailed,
            IOException, NegotiatorException {

        while (in.remaining() > 0) {
            TLSPlaintext tlsPlaintext = new ProcessingTLSPlaintext()
                    .set(output).set(context).in(in).run().tlsPlaintext();

            if (tlsPlaintext.containsChangeCipherSpec()) {
                processChangeCipherSpec(tlsPlaintext);
                continue;
            }

            ContentType type;
            ByteBuffer content;
            if (encryptedHandshakeData()) {
                TLSInnerPlaintext tlsInnerPlaintext = new ProcessingTLSCiphertext(Phase.handshake)
                        .set(output).set(context).set(tlsPlaintext).run().tlsInnerPlaintext();
                type = tlsInnerPlaintext.getType();
                content = ByteBuffer.wrap(tlsInnerPlaintext.getContent());
            } else if (encryptedApplicationData()) {
                TLSInnerPlaintext tlsInnerPlaintext = new ProcessingTLSCiphertext(Phase.application_data)
                        .set(output).set(context).set(tlsPlaintext).run().tlsInnerPlaintext();
                type = tlsInnerPlaintext.getType();
                content = ByteBuffer.wrap(tlsInnerPlaintext.getContent());
            } else {
                type = tlsPlaintext.getType();
                content = ByteBuffer.wrap(tlsPlaintext.getFragment());
            }

            if (ContentType.handshake.equals(type)) {
                processHandshake(content);
                continue;
            }

            if (ContentType.alert.equals(type)) {
                processAlert(content);
                continue;
            }

            if (ContentType.application_data.equals(type)) {
                processApplicationData(content);
                continue;
            }

            throw new IllegalStateException("what the hell? unexpected content type!");
        }

        return this;
    }

    private boolean encryptedHandshakeData() {
        return context.hasServerHello() && !context.hasServerFinished();
    }

    private boolean encryptedApplicationData() {
        return context.hasServerFinished();
    }

    private void processHandshake(ByteBuffer buffer)
            throws ActionFailed, NegotiatorException, IOException, AEADException {

        while (buffer.remaining() > 0) {
            Handshake handshake = new ProcessingHandshake()
                    .set(output).set(context).in(buffer).run().handshake();

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
        processChangeCipherSpec(ByteBuffer.wrap(tlsPlaintext.getFragment()));
    }

    private void processChangeCipherSpec(ByteBuffer buffer) throws ActionFailed {
        new ProcessingChangeCipherSpec().set(output).set(context).in(buffer).run();
    }

    private void processAlert(ByteBuffer buffer) {
        new ProcessingAlert().set(output).set(context).in(buffer).run();
    }

    private void processApplicationData(ByteBuffer buffer) {
        context.addApplicationData(buffer.array());
        new PrintingData().set(output).set(context).in(buffer).run();
    }

    private void processClientHello(Handshake handshake) {
        throw new UnsupportedOperationException("no message processing for you!");
    }

    private void processServerHello(Handshake handshake)
            throws IOException, AEADException, NegotiatorException {

        new ProcessingServerHello().set(output).set(context)
                .in(handshake.getBody()).run();

        context.setServerHello(handshake);

        new NegotiatingDHSecret().set(output).set(context).run();
        new ComputingKeysAfterServerHello().set(output).set(context).run();
    }

    private void processHelloRetryRequest(Handshake handshake) {
        throw new UnsupportedOperationException("no message processing for you!");
    }

    private void processEncryptedExtensions(Handshake handshake) {
        new ProcessingEncryptedExtensions().set(output).set(context)
                .in(handshake.getBody()).run();

        context.setEncryptedExtensions(handshake);
    }

    private void processCertificate(Handshake handshake) {
        new ProcessingCertificate().set(output).set(context)
                .in(handshake.getBody()).run();

        context.setServerCertificate(handshake);
    }

    private void processCertificateVerify(Handshake handshake) {
        new ProcessingCertificateVerify().set(output).set(context)
                .in(handshake.getBody()).run();

        context.setServerCertificateVerify(handshake);
    }

    private void processFinished(Handshake handshake)
            throws IOException, ActionFailed {

        context.setServerFinished(handshake);

        new ProcessingFinished().set(output).set(context)
                .in(handshake.getBody()).run();

        new ComputingKeysAfterServerFinished().set(output).set(context).run();
    }

    private void processNewSessionTicket(Handshake handshake) throws IOException {
        new ProcessingNewSessionTicket().set(output).set(context)
                .in(handshake.getBody()).run();
    }

}
