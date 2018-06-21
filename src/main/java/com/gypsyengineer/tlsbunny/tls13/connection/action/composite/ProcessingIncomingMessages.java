package com.gypsyengineer.tlsbunny.tls13.connection.action.composite;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.ActionFailed;
import com.gypsyengineer.tlsbunny.tls13.connection.action.simple.ProcessingHandshake;
import com.gypsyengineer.tlsbunny.tls13.connection.action.simple.ProcessingTLSPlaintext;
import com.gypsyengineer.tlsbunny.tls13.crypto.AEADException;
import com.gypsyengineer.tlsbunny.tls13.handshake.NegotiatorException;
import com.gypsyengineer.tlsbunny.tls13.struct.*;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ProcessingIncomingMessages extends AbstractAction<ProcessingIncomingMessages> {

    @Override
    public String name() {
        return "incoming messages";
    }

    @Override
    public ProcessingIncomingMessages run() throws AEADException, ActionFailed,
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

    private void processHandshake(TLSPlaintext tlsPlaintext) throws ActionFailed {
        ByteBuffer buffer = ByteBuffer.wrap(tlsPlaintext.getFragment());
        while (buffer.remaining() > 0) {
            Handshake handshake = new ProcessingHandshake()
                    .set(output).set(context).run().handshake();

            if (handshake.containsServerHello()) {
                processServerHello(handshake);
                continue;
            }

            throw new IllegalStateException("what the hell? unexpected handshake message!");
        }
    }

    private void processChangeCipherSpec(TLSPlaintext tlsPlaintext) {

    }

    private void processAlert(TLSPlaintext tlsPlaintext) {

    }

    private void processApplicationData(TLSPlaintext tlsPlaintext) {

    }

    private void processServerHello(Handshake handshake) {

    }

    private void processEncryptedExtensions(Handshake handshake) {

    }

    private void processCertificate(Handshake handshake) {

    }

    private void processCertificateVerify(Handshake handshake) {

    }

    private void processFinished(Handshake handshake) {

    }

}
