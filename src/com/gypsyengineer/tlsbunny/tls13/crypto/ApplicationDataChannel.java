package com.gypsyengineer.tlsbunny.tls13.crypto;

import com.gypsyengineer.tlsbunny.tls.UInt16;
import com.gypsyengineer.tlsbunny.tls13.struct.*;

import static com.gypsyengineer.tlsbunny.tls13.struct.TLSInnerPlaintext.NO_PADDING;

import com.gypsyengineer.tlsbunny.utils.Connection;
import com.gypsyengineer.tlsbunny.utils.Utils;

import java.io.IOException;

public class ApplicationDataChannel {

    public static final int TAG_LENGTH = 16;

    private final AEAD encryptor;
    private final AEAD decryptor;
    private final Connection connection;
    private final StructFactory factory;

    public ApplicationDataChannel(StructFactory factory, Connection connection,
            AEAD enctyptor, AEAD decryptor) {

        this.connection = connection;
        this.encryptor = enctyptor;
        this.decryptor = decryptor;
        this.factory = factory;
    }

    public byte[] receive() throws Exception {
        byte[] bytes = connection.read();
        if (bytes.length == 0) {
            // TODO: is it correct?
            return bytes;
        }
        TLSPlaintext tlsCiphertext = factory.parser().parseTLSPlaintext(bytes);

        if (!tlsCiphertext.containsApplicationData()) {
            throw new RuntimeException();
        }

        return decrypt(tlsCiphertext.getFragment(), getAdditionalData(tlsCiphertext));
    }

    public void send(byte[] data) throws Exception {
        TLSPlaintext[] tlsPlaintexts = factory.createTLSPlaintexts(ContentType.application_data,
                ProtocolVersion.TLSv12,
                encrypt(data));

        for (TLSPlaintext tlsPlaintext : tlsPlaintexts) {
            connection.send(tlsPlaintext.encoding());
        }
    }

    public byte[] decrypt(TLSPlaintext tlsCiphertext) throws Exception {
        return decrypt(tlsCiphertext.getFragment(), getAdditionalData(tlsCiphertext));
    }

    public byte[] decrypt(byte[] ciphertext, byte[] additional_data) throws Exception {
        decryptor.start();
        decryptor.update(ciphertext);
        decryptor.updateAAD(additional_data);
        byte[] plaintext = decryptor.finish();

        return factory.parser().parseTLSInnerPlaintext(plaintext).getContent();
    }

    public byte[] encrypt(byte[] data) throws Exception {
        TLSInnerPlaintext tlsInnerPlaintext = factory.createTLSInnerPlaintext(
                ContentType.application_data, data, NO_PADDING);
        byte[] plaintext = tlsInnerPlaintext.encoding();

        encryptor.start();
        encryptor.update(plaintext);
        encryptor.updateAAD(getAdditionalData(
                ContentType.application_data,
                ProtocolVersion.TLSv12,
                new UInt16(plaintext.length + TAG_LENGTH)));

        return encryptor.finish();
    }

    public boolean isAlive() {
        return connection.isAlive();
    }

    private byte[] getAdditionalData(ContentType type, ProtocolVersion version, UInt16 length) throws IOException {
        return Utils.concatenate(type.encoding(), version.encoding(), length.encoding());
    }

    private byte[] getAdditionalData(TLSPlaintext tlsPlaintext) throws IOException {
        return getAdditionalData(tlsPlaintext.getType(), tlsPlaintext.getLegacyRecordVersion(), tlsPlaintext.getFragmentLength());
    }

}
