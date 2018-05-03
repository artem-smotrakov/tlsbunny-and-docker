package com.gypsyengineer.tlsbunny.tls13.connection.action.composite;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;
import com.gypsyengineer.tlsbunny.tls13.connection.action.ActionFailed;
import com.gypsyengineer.tlsbunny.tls13.crypto.AEAD;
import com.gypsyengineer.tlsbunny.tls13.crypto.AEADException;
import com.gypsyengineer.tlsbunny.tls13.crypto.AesGcm;
import com.gypsyengineer.tlsbunny.tls13.crypto.TranscriptHash;
import com.gypsyengineer.tlsbunny.tls13.struct.*;
import com.gypsyengineer.tlsbunny.tls13.utils.TLS13Utils;
import com.gypsyengineer.tlsbunny.utils.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
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
    public Action run() throws IOException, AEADException, ActionFailed {
        CertificateVerify certificateVerify = createCertificateVerify();
        Handshake handshake = toHandshake(certificateVerify);

        // TODO: the class should be renamed to OutgoingClientCertificateVerify
        //       since it sets a client CertificateVerify in context
        context.setClientCertificateVerify(handshake);

        out = TLS13Utils.store(encrypt(handshake));

        return this;
    }

    private CertificateVerify createCertificateVerify() throws IOException, ActionFailed {

        try {
            byte[] content = Utils.concatenate(
                    CERTIFICATE_VERIFY_PREFIX,
                    CERTIFICATE_VERIFY_CONTEXT_STRING,
                    new byte[]{0},
                    TranscriptHash.compute(context.suite.hash(), context.allMessages()));

            Signature signature = Signature.getInstance("SHA256withECDSA");
            signature.initSign(
                    KeyFactory.getInstance("EC").generatePrivate(
                            new PKCS8EncodedKeySpec(key_data)));
            signature.update(content);

            return context.factory.createCertificateVerify(
                    SignatureScheme.ecdsa_secp256r1_sha256, signature.sign());
        } catch (NoSuchAlgorithmException | SignatureException
                | InvalidKeyException | InvalidKeySpecException e) {

            // TODO: can we get rid of these exceptions?
            throw new ActionFailed(e);
        }
    }

    // TODO: move this method to handshakeDecryptor to avoid code duplicates
    //       run other classes for outgoing handshake messages
    TLSPlaintext[] encrypt(Handshake message) throws IOException, AEADException {
        return context.factory.createTLSPlaintexts(
                ContentType.application_data,
                ProtocolVersion.TLSv12,
                encrypt(message.encoding()));
    }

    // TODO: move this method to handshakeDecryptor to avoid code duplicates
    //       run other classes for outgoing handshake messages
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
