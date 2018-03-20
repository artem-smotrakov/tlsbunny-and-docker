package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.crypto.TranscriptHash;
import com.gypsyengineer.tlsbunny.tls13.struct.ContentType;
import com.gypsyengineer.tlsbunny.tls13.struct.Finished;
import com.gypsyengineer.tlsbunny.tls13.struct.Handshake;
import com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSInnerPlaintext;
import static com.gypsyengineer.tlsbunny.tls13.struct.TLSInnerPlaintext.NO_PADDING;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;

public class OutgoingFinished extends AbstractAction {

    @Override
    public Action run() {
        try {
            Finished finished = createFinished();
            Handshake handshake = toHandshake(finished);
            context.setClientFinished(handshake);

            byte[] zero_hash_value = new byte[0];
            context.resumption_master_secret = hkdf.deriveSecret(
                    context.master_secret,
                    context.res_master,
                    context.allMessages());
            context.client_application_write_key = hkdf.expandLabel(
                    context.client_application_traffic_secret_0,
                    context.key,
                    zero_hash_value,
                    suite.keyLength());
            context.client_application_write_iv = hkdf.expandLabel(
                    context.client_application_traffic_secret_0,
                    context.iv,
                    zero_hash_value,
                    suite.ivLength());
            context.server_application_write_key = hkdf.expandLabel(
                    context.server_application_traffic_secret_0,
                    context.key,
                    zero_hash_value,
                    suite.keyLength());
            context.server_application_write_iv = hkdf.expandLabel(
                    context.server_application_traffic_secret_0,
                    context.iv,
                    zero_hash_value,
                    suite.ivLength());

            connection.send(encrypt(factory.createTLSInnerPlaintext(ContentType.handshake, handshake.encoding(), NO_PADDING)));

            succeeded = true;
        } catch (Exception e) {
            succeeded = false;
        }

        return this;
    }

    private Finished createFinished() throws Exception {
        byte[] verify_data = hkdf.hmac(
                context.finished_key,
                TranscriptHash.compute(suite.hash(), context.allMessages()));

        return factory.createFinished(verify_data);
    }

    TLSPlaintext[] encrypt(Handshake message) throws Exception {
        return encrypt(factory.createTLSInnerPlaintext(
                ContentType.handshake, message.encoding(), NO_PADDING));
    }

    private TLSPlaintext[] encrypt(TLSInnerPlaintext message) throws Exception {
        return factory.createTLSPlaintexts(ContentType.application_data,
                ProtocolVersion.TLSv12,
                context.handshakeEncryptor.encrypt(message.encoding()));
    }

}
