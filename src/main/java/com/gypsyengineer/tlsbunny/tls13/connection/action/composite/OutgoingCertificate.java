package com.gypsyengineer.tlsbunny.tls13.connection.action.composite;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;
import com.gypsyengineer.tlsbunny.tls13.crypto.AEAD;
import com.gypsyengineer.tlsbunny.tls13.crypto.AEADException;
import com.gypsyengineer.tlsbunny.tls13.crypto.AesGcm;
import com.gypsyengineer.tlsbunny.tls13.struct.*;
import com.gypsyengineer.tlsbunny.tls13.utils.Helper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.gypsyengineer.tlsbunny.tls13.struct.TLSInnerPlaintext.NO_PADDING;

public class OutgoingCertificate extends AbstractAction {

    private byte[] cert_data;

    public Action certificate(String path) throws IOException {
        if (path == null || path.trim().isEmpty()) {
            throw  new IllegalArgumentException("no certificate specified");
        }

        cert_data = Files.readAllBytes(Paths.get(path));

        return this;
    }

    @Override
    public String name() {
        return "Certificate";
    }

    @Override
    public Action run() throws IOException, AEADException {
        Certificate certificate = createCertificate();
        Handshake handshake = toHandshake(certificate);

        // TODO: the class should be renamed to OutgoingClientCertificate
        //       since it sets a client certificate in context
        context.setClientCertificate(handshake);

        out = Helper.store(encrypt(handshake));

        return this;
    }

    private Certificate createCertificate() throws IOException {
        return context.factory.createCertificate(
                context.certificate_request_context.bytes(),
                context.factory.createX509CertificateEntry(cert_data));
    }


    TLSPlaintext[] encrypt(Handshake message) throws IOException, AEADException {
        return context.factory.createTLSPlaintexts(
                ContentType.application_data,
                ProtocolVersion.TLSv12,
                encrypt(message.encoding()));
    }

    private byte[] encrypt(byte[] data) throws IOException, AEADException {
        TLSInnerPlaintext tlsInnerPlaintext = context.factory.createTLSInnerPlaintext(
                ContentType.handshake, data, NO_PADDING);
        byte[] plaintext = tlsInnerPlaintext.encoding();

        context.handshakeEncryptor.start();
        context.handshakeEncryptor.updateAAD(
                AEAD.getAdditionalData(plaintext.length + AesGcm.TAG_LENGTH_IN_BYTES));
        context.handshakeEncryptor.update(plaintext);

        return context.handshakeEncryptor.finish();
    }

}
