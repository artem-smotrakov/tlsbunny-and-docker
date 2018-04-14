package com.gypsyengineer.tlsbunny.tls13.connection.action.simple;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;
import com.gypsyengineer.tlsbunny.tls13.crypto.TranscriptHash;
import com.gypsyengineer.tlsbunny.tls13.struct.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class GeneratingFinished extends AbstractAction {

    @Override
    public String name() {
        return "generating Finished";
    }

    @Override
    public Action run() throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        byte[] verify_data = hkdf.hmac(
                context.finished_key,
                TranscriptHash.compute(suite.hash(), context.allMessages()));

        Finished finished = factory.createFinished(verify_data);
        out = ByteBuffer.wrap(finished.encoding());

        return this;
    }
}
