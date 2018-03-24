package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.crypto.AEAD;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSInnerPlaintext;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;

import java.nio.ByteBuffer;

import static com.gypsyengineer.tlsbunny.utils.Utils.info;

public class IncomingApplicationData extends AbstractReceivingAction {

    @Override
    boolean runImpl(ByteBuffer buffer) throws Exception {
        TLSPlaintext tlsCiphertext = factory.parser().parseTLSPlaintext(buffer);

        if (!tlsCiphertext.containsApplicationData()) {
            throw new RuntimeException();
        }

        TLSInnerPlaintext tlsInnerPlaintext = decrypt(tlsCiphertext);

        if (!tlsInnerPlaintext.containsApplicationData()) {
            return false;
        }

        byte[] data = tlsInnerPlaintext.getContent();
        info("received data (%d bytes)%n%s", data.length, new String(data));

        return true;
    }

    private TLSInnerPlaintext decrypt(TLSPlaintext tlsCiphertext) throws Exception {
        return decrypt(tlsCiphertext.getFragment(), AEAD.getAdditionalData(tlsCiphertext));
    }

    private TLSInnerPlaintext decrypt(byte[] ciphertext, byte[] additional_data)
            throws Exception {

        context.applicationDataDecryptor.start();
        context.applicationDataDecryptor.updateAAD(additional_data);
        context.applicationDataDecryptor.update(ciphertext);
        byte[] plaintext = context.applicationDataDecryptor.finish();

        return factory.parser().parseTLSInnerPlaintext(plaintext);
    }
}
