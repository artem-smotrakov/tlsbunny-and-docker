package com.gypsyengineer.tlsbunny.tls13.connection.action.simple;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;
import com.gypsyengineer.tlsbunny.tls13.crypto.TranscriptHash;
import com.gypsyengineer.tlsbunny.tls13.struct.Finished;

import java.util.Arrays;

import static com.gypsyengineer.tlsbunny.tls13.handshake.Context.ZERO_HASH_VALUE;

public class ProcessingFinished extends AbstractAction {

    @Override
    public String name() {
        return "processing a Finished";
    }

    @Override
    public Action run() throws Exception {
        Finished finished = context.factory.parser().parseFinished(in, context.suite.hashLength());

        byte[] verify_key = context.hkdf.expandLabel(
                context.server_handshake_traffic_secret,
                context.finished,
                ZERO_HASH_VALUE,
                context.hkdf.getHashLength());

        byte[] verify_data = context.hkdf.hmac(
                verify_key,
                TranscriptHash.compute(context.suite.hash(), context.allMessages()));

        boolean success = Arrays.equals(verify_data, finished.getVerifyData());
        if (!success) {
            throw new RuntimeException("verification of Finished failed");
        }

        context.verifyServerFinished();

        output.info("verified Finished message");

        return this;
    }

}
