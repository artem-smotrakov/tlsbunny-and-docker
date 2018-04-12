package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.crypto.AEAD;
import com.gypsyengineer.tlsbunny.tls13.crypto.AesGcm;
import com.gypsyengineer.tlsbunny.tls13.crypto.TranscriptHash;
import com.gypsyengineer.tlsbunny.tls13.struct.*;
import com.gypsyengineer.tlsbunny.tls13.utils.Helper;
import com.gypsyengineer.tlsbunny.utils.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;

import static com.gypsyengineer.tlsbunny.tls13.struct.TLSInnerPlaintext.NO_PADDING;

public class OutgoingCertificateVerify extends AbstractAction {

    private static final byte[] CERTIFICATE_VERIFY_PREFIX = new byte[64];
    static {
        for (int i=0; i<CERTIFICATE_VERIFY_PREFIX.length; i++) {
            CERTIFICATE_VERIFY_PREFIX[i] = 0x20;
        }
    }

    private static final byte[] CERTIFICATE_VERIFY_CONTEXT_STRING =
            "TLS 1.3, client CertificateVerify".getBytes();

    private byte[] key_data;

    public Action key(String path) throws IOException {
        if (path == null || path.trim().isEmpty()) {
            throw  new IllegalArgumentException("no certificate key specified");
        }

        key_data = Files.readAllBytes(Paths.get(path));

        return this;
    }

    @Override
    public String name() {
        return "CertificateVerify";
    }

    @Override
    public Action run() throws Exception {
        CertificateVerify certificateVerify = createCertificateVerify();
        Handshake handshake = toHandshake(certificateVerify);

        // TODO: the class should be renamed to OutgoingClientCertificateVerify
        //       since it sets a client CertificateVerify in context
        context.setClientCertificateVerify(handshake);

        out = Helper.store(encrypt(handshake));

        return this;
    }

    private CertificateVerify createCertificateVerify() throws Exception {
        byte[] content = Utils.concatenate(
                CERTIFICATE_VERIFY_PREFIX,
                CERTIFICATE_VERIFY_CONTEXT_STRING,
                new byte[] { 0 },
                TranscriptHash.compute(suite.hash(), context.allMessages()));

        Signature signature = Signature.getInstance("SHA256withECDSA");
        signature.initSign(
                KeyFactory.getInstance("EC").generatePrivate(
                        new PKCS8EncodedKeySpec(key_data)));
        signature.update(content);

        return factory.createCertificateVerify(
                SignatureScheme.ecdsa_secp256r1_sha256, signature.sign());
    }

    // TODO: move this method to handshakeDecryptor to avoid code duplicates
    //       run other classes for outgoing handshake messages
    TLSPlaintext[] encrypt(Handshake message) throws Exception {
        return factory.createTLSPlaintexts(
                ContentType.application_data,
                ProtocolVersion.TLSv12,
                encrypt(message.encoding()));
    }

    // TODO: move this method to handshakeDecryptor to avoid code duplicates
    //       run other classes for outgoing handshake messages
    private byte[] encrypt(byte[] data) throws Exception {
        TLSInnerPlaintext tlsInnerPlaintext = factory.createTLSInnerPlaintext(
                ContentType.handshake, data, NO_PADDING);
        byte[] plaintext = tlsInnerPlaintext.encoding();

        context.handshakeEncryptor.start();
        context.handshakeEncryptor.updateAAD(
                AEAD.getAdditionalData(plaintext.length + AesGcm.TAG_LENGTH_IN_BYTES));
        context.handshakeEncryptor.update(plaintext);

        return context.handshakeEncryptor.finish();
    }

}
