package com.gypsyengineer.tlsbunny.tls13.connection.action.simple;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;
import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.tls13.struct.*;
import com.gypsyengineer.tlsbunny.tls13.utils.Helper;

import java.nio.ByteBuffer;

public class WrappingIntoTLSPlaintexts extends AbstractAction {

    private ContentType type;
    private ProtocolVersion version;

    public WrappingIntoTLSPlaintexts type(ContentType type) {
        this.type = type;
        return this;
    }

    public WrappingIntoTLSPlaintexts version(ProtocolVersion version) {
        this.version = version;
        return this;
    }

    @Override
    public String name() {
        return String.format("wrapping into TLSPlaintext (%s, %s)", type, version);
    }

    @Override
    public Action run() throws Exception {
        out = Helper.store(factory.createTLSPlaintexts(type, version, in.array()));

        return this;
    }

}
