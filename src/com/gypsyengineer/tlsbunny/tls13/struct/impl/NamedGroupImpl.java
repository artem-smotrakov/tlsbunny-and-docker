package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup;

public class NamedGroupImpl implements NamedGroup {


    public final int code;

    private NamedGroupImpl(int code) {
        this.code = code;
    }

    @Override
    public int encodingLength() {
        return ENCODING_LENGTH;
    }

    @Override
    public byte[] encoding() throws IOException {
        return ByteBuffer.allocate(ENCODING_LENGTH).putShort((short) code).array();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + this.code;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj == null) {
            return false;
        }
        
        if (obj instanceof NamedGroupImpl == false) {
            return false;
        }
        
        NamedGroupImpl other = (NamedGroupImpl) obj;
        return this.code == other.code;
    }

    public static NamedGroupImpl parse(ByteBuffer buffer) {
        return new NamedGroupImpl(buffer.getShort());
    }
    
    public static class SecpImpl extends NamedGroupImpl implements Secp {

        private final String curve;
        
        public SecpImpl(int code, String curve) {
            super(code);
            this.curve = curve;
        }

        @Override
        public String getCurve() {
            return curve;
        }

    }

    public static class XImpl extends NamedGroupImpl implements X {

        public XImpl(int code) {
            super(code);
        }

    }

    public static class FFDHEImpl extends NamedGroupImpl implements FFDHE {

        public FFDHEImpl(int code) {
            super(code);
        }

    }

}
