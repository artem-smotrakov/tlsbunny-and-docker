package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.crypto.AEAD;
import com.gypsyengineer.tlsbunny.tls13.crypto.AesGcm;
import com.gypsyengineer.tlsbunny.tls13.crypto.TranscriptHash;
import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.tls13.struct.ContentType;
import com.gypsyengineer.tlsbunny.tls13.struct.Finished;
import com.gypsyengineer.tlsbunny.tls13.struct.Handshake;
import com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSInnerPlaintext;

import static com.gypsyengineer.tlsbunny.tls13.handshake.Context.ZERO_HASH_VALUE;
import static com.gypsyengineer.tlsbunny.tls13.struct.TLSInnerPlaintext.NO_PADDING;

import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;

public class OutgoingFinished extends AbstractAction {

    @Override
    public String description() {
        return "send Finished";
    }

    @Override
    public Action run() throws Exception {
        Finished finished = createFinished();
        Handshake handshake = toHandshake(finished);
        context.setClientFinished(handshake);

        context.resumption_master_secret = hkdf.deriveSecret(
                context.master_secret,
                Context.res_master,
                context.allMessages());
        context.client_application_write_key = hkdf.expandLabel(
                context.client_application_traffic_secret_0,
                Context.key,
                ZERO_HASH_VALUE,
                suite.keyLength());
        context.client_application_write_iv = hkdf.expandLabel(
                context.client_application_traffic_secret_0,
                Context.iv,
                ZERO_HASH_VALUE,
                suite.ivLength());
        context.server_application_write_key = hkdf.expandLabel(
                context.server_application_traffic_secret_0,
                Context.key,
                ZERO_HASH_VALUE,
                suite.keyLength());
        context.server_application_write_iv = hkdf.expandLabel(
                context.server_application_traffic_secret_0,
                Context.iv,
                ZERO_HASH_VALUE,
                suite.ivLength());

        connection.send(encrypt(handshake));

        context.applicationDataEnctyptor = AEAD.createEncryptor(
                suite.cipher(),
                context.client_application_write_key,
                context.client_application_write_iv);
        context.applicationDataDecryptor = AEAD.createDecryptor(
                suite.cipher(),
                context.server_application_write_key,
                context.server_application_write_iv);

        return this;
    }

    private Finished createFinished() throws Exception {
        byte[] verify_data = hkdf.hmac(
                context.finished_key,
                TranscriptHash.compute(suite.hash(), context.allMessages()));

        return factory.createFinished(verify_data);
    }

    TLSPlaintext[] encrypt(Handshake message) throws Exception {
        return factory.createTLSPlaintexts(
                ContentType.application_data,
                ProtocolVersion.TLSv12,
                encrypt(message.encoding()));
    }

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
