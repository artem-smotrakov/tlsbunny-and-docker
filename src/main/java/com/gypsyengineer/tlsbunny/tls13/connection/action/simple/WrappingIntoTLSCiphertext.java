package com.gypsyengineer.tlsbunny.tls13.connection.action.simple;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Phase;
import com.gypsyengineer.tlsbunny.tls13.crypto.AEAD;
import com.gypsyengineer.tlsbunny.tls13.crypto.AEADException;
import com.gypsyengineer.tlsbunny.tls13.crypto.AesGcm;
import com.gypsyengineer.tlsbunny.tls13.struct.ContentType;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSInnerPlaintext;
import com.gypsyengineer.tlsbunny.tls13.utils.TLS13Utils;

import java.io.IOException;

import static com.gypsyengineer.tlsbunny.tls13.struct.ContentType.application_data;
import static com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion.TLSv12;
import static com.gypsyengineer.tlsbunny.tls13.struct.TLSInnerPlaintext.NO_PADDING;

public class WrappingIntoTLSCiphertext extends AbstractAction {

    private final Phase phase;
    private ContentType type;

    public WrappingIntoTLSCiphertext(Phase phase) {
        this.phase = phase;
    }

    public WrappingIntoTLSCiphertext type(ContentType type) {
        this.type = type;
        return this;
    }

    @Override
    public String name() {
        return String.format("wrapping into TLSCiphertext (%s)", phase);
    }

    @Override
    public Action run() throws AEADException, IOException {
        byte[] content = new byte[in.remaining()];
        in.get(content);

        AEAD encryptor;
        switch (phase) {
            case handshake:
                encryptor = context.handshakeEncryptor;
                break;
            case application_data:
                encryptor = context.applicationDataEnctyptor;
                break;
            default:
                throw new IllegalArgumentException(
                        String.format("what the hell? unknown phase: %s", phase));
        }

        if (encryptor == null) {
            throw new IllegalStateException(
                    "what the hell? encryptor is not initialized (null)");
        }

        TLSInnerPlaintext tlsInnerPlaintext = context.factory.createTLSInnerPlaintext(
                type, content, NO_PADDING);
        byte[] plaintext = tlsInnerPlaintext.encoding();

        encryptor.start();
        encryptor.updateAAD(
                AEAD.getAdditionalData(plaintext.length + AesGcm.TAG_LENGTH_IN_BYTES));
        encryptor.update(plaintext);
        byte[] ciphertext = encryptor.finish();

        out = TLS13Utils.store(context.factory.createTLSPlaintexts(application_data, TLSv12, ciphertext));

        return this;
    }

}
