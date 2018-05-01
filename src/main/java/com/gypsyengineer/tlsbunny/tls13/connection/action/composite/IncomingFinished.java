package com.gypsyengineer.tlsbunny.tls13.connection.action.composite;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;
import com.gypsyengineer.tlsbunny.tls13.connection.action.ActionFailed;
import com.gypsyengineer.tlsbunny.tls13.crypto.AEADException;
import com.gypsyengineer.tlsbunny.tls13.crypto.TranscriptHash;
import com.gypsyengineer.tlsbunny.tls13.struct.Finished;
import com.gypsyengineer.tlsbunny.tls13.struct.Handshake;

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
    public Action run() throws ActionFailed, IOException, AEADException {
        Handshake handshake = processEncryptedHandshake();
        if (!handshake.containsFinished()) {
            throw new ActionFailed("expected a Finished message");
        }

        try {
            processFinished(handshake);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new ActionFailed(e);
        }

        return this;
    }

    private void processFinished(Handshake handshake)
            throws NoSuchAlgorithmException, IOException, InvalidKeyException {

        Finished message = context.factory.parser().parseFinished(
                handshake.getBody(),
                context.suite.hashLength());

        byte[] verify_key = context.hkdf.expandLabel(
                context.server_handshake_traffic_secret,
                context.finished,
                ZERO_HASH_VALUE,
                context.hkdf.getHashLength());

        byte[] verify_data = context.hkdf.hmac(
                verify_key,
                TranscriptHash.compute(context.suite.hash(), context.allMessages()));

        boolean success = Arrays.equals(verify_data, message.getVerifyData());
        if (!success) {
            throw new RuntimeException();
        }

        context.setServerFinished(handshake);
        context.verifyServerFinished();

        context.client_application_traffic_secret_0 = context.hkdf.deriveSecret(
                context.master_secret,
                context.c_ap_traffic,
                context.allMessages());
        context.server_application_traffic_secret_0 = context.hkdf.deriveSecret(
                context.master_secret,
                context.s_ap_traffic,
                context.allMessages());
        context.exporter_master_secret = context.hkdf.deriveSecret(
                context.master_secret,
                context.exp_master,
                context.allMessages());
    }
}
