package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.crypto.AEAD;
import com.gypsyengineer.tlsbunny.tls13.crypto.AesGcm;
import com.gypsyengineer.tlsbunny.tls13.struct.ContentType;
import com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSInnerPlaintext;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;

import static com.gypsyengineer.tlsbunny.tls13.struct.TLSInnerPlaintext.NO_PADDING;

public class OutgoingApplicationData extends AbstractAction {

    public static final byte[] NOTHING = new byte[0];

    private byte[] data = NOTHING;

    public OutgoingApplicationData(byte[] data) {
        this.data = data;
    }

    public OutgoingApplicationData(String string) {
        this(string.getBytes());
    }

    @Override
    public String description() {
        return "send application data";
    }

    @Override
    public Action run() throws Exception {
        TLSPlaintext[] tlsPlaintexts = factory.createTLSPlaintexts(
                ContentType.application_data,
                ProtocolVersion.TLSv12,
                encrypt(data));

        for (TLSPlaintext tlsPlaintext : tlsPlaintexts) {
            connection.send(tlsPlaintext.encoding());
        }


        return this;
    }

    private byte[] encrypt(byte[] data) throws Exception {
        TLSInnerPlaintext tlsInnerPlaintext = factory.createTLSInnerPlaintext(
                ContentType.application_data, data, NO_PADDING);
        byte[] plaintext = tlsInnerPlaintext.encoding();

        context.applicationDataEnctyptor.start();
        context.applicationDataEnctyptor.updateAAD(
                AEAD.getAdditionalData(plaintext.length + AesGcm.TAG_LENGTH_IN_BYTES));
        context.applicationDataEnctyptor.update(plaintext);

        return context.applicationDataEnctyptor.finish();
    }
}
