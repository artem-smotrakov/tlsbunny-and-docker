package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.crypto.TranscriptHash;
import com.gypsyengineer.tlsbunny.tls13.struct.Finished;
import com.gypsyengineer.tlsbunny.tls13.struct.Handshake;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSInnerPlaintext;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static com.gypsyengineer.tlsbunny.tls13.handshake.Context.ZERO_HASH_VALUE;

public class IncomingFinished extends AbstractAction {

    @Override
    public String name() {
        return "Finished";
    }

    @Override
    public Action run() throws Exception {
        Handshake handshake = processEncryptedHandshake();
        if (!handshake.containsFinished()) {
            throw new IOException("expected a Finished message");
        }

        processFinished(handshake);

        return this;
    }

    private void processFinished(Handshake handshake)
            throws NoSuchAlgorithmException, IOException, InvalidKeyException {

        Finished message = factory.parser().parseFinished(
                handshake.getBody(),
                suite.hashLength());

        byte[] verify_key = hkdf.expandLabel(
                context.server_handshake_traffic_secret,
                context.finished,
                ZERO_HASH_VALUE,
                hkdf.getHashLength());

        byte[] verify_data = hkdf.hmac(
                verify_key,
                TranscriptHash.compute(suite.hash(), context.allMessages()));

        boolean success = Arrays.equals(verify_data, message.getVerifyData());
        if (!success) {
            throw new RuntimeException();
        }

        context.setServerFinished(handshake);

        context.client_application_traffic_secret_0 = hkdf.deriveSecret(
                context.master_secret,
                context.c_ap_traffic,
                context.allMessages());
        context.server_application_traffic_secret_0 = hkdf.deriveSecret(
                context.master_secret,
                context.s_ap_traffic,
                context.allMessages());
        context.exporter_master_secret = hkdf.deriveSecret(
                context.master_secret,
                context.exp_master,
                context.allMessages());
    }
}
