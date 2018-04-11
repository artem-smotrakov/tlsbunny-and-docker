package com.gypsyengineer.tlsbunny.tls13.connection;

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

public class ComplexAction implements Action {

    private final String name;
    private final List<Action> actions = new ArrayList<>();
    private ByteBuffer buffer;

    public ComplexAction(String name, Action... actions) {
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
    public Action set(StructFactory factory) {
        for (Action action : actions) {
            action.set(factory);
        }

        return this;
    }

    @Override
    public Action set(SignatureScheme scheme) {
        for (Action action : actions) {
            action.set(scheme);
        }

        return this;
    }

    @Override
    public Action set(NamedGroup group) {
        for (Action action : actions) {
            action.set(group);
        }

        return this;
    }

    @Override
    public Action set(CipherSuite suite) {
        for (Action action : actions) {
            action.set(suite);
        }

        return this;
    }

    @Override
    public Action set(Negotiator negotiator) {
        for (Action action : actions) {
            action.set(negotiator);
        }

        return this;
    }

    @Override
    public Action set(HKDF hkdf) {
        for (Action action : actions) {
            action.set(hkdf);
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
        }

        return this;
    }

    @Override
    public ByteBuffer data() {
        return buffer;
    }
}
