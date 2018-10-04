package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.ResponderID;

import java.io.IOException;

public class ResponderIDImpl implements ResponderID {

    private Vector<Byte> content;

    ResponderIDImpl(Vector<Byte> content) {
        this.content = content;
    }

    @Override
    public Vector<Byte> getContent() {
        return content;
    }

    @Override
    public int encodingLength() {
        return ResponderID.LENGTH_BYTES;
    }

    @Override
    public byte[] encoding() throws IOException {
        return content.encoding();
    }

    @Override
    public ResponderIDImpl copy() {
        return new ResponderIDImpl((Vector<Byte>) content.copy());
    }
}
