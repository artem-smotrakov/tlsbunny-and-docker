package com.gypsyengineer.tlsbunny.tls13.connection.action;

import com.gypsyengineer.tlsbunny.tls13.crypto.HKDF;
import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.tls13.handshake.Negotiator;
import com.gypsyengineer.tlsbunny.tls13.struct.CipherSuite;
import com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup;
import com.gypsyengineer.tlsbunny.tls13.struct.SignatureScheme;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ActionChain implements Action {

    private final String name;
    private final List<Action> actions = new ArrayList<>();
    private ByteBuffer buffer;

    public ActionChain(String name, Action... actions) {
        this.name = name;
        for (Action action : actions) {
            this.actions.add(action);
        }
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Action set(Output output) {
        for (Action action : actions) {
            action.set(output);
        }

        return this;
    }

    @Override
    public Action set(Context context) {
        for (Action action : actions) {
            action.set(context);
        }

        return this;
    }

    @Override
    public Action set(ByteBuffer buffer) {
        this.buffer = buffer;
        for (Action action : actions) {
            action.set(buffer);
        }

        return this;
    }

    @Override
    public Action run() throws Exception {
        for (Action action : actions) {
            action.run();

            if (action.produced()) {
                byte[] unprocessed = new byte[buffer.remaining()];
                buffer.get(unprocessed);
                buffer.clear();
                buffer.put(action.data());
                buffer.put(unprocessed);
            }
        }

        return this;
    }

    @Override
    public boolean produced() {
        return buffer != null && buffer.remaining() > 0;
    }

    @Override
    public ByteBuffer data() {
        return buffer;
    }
}
