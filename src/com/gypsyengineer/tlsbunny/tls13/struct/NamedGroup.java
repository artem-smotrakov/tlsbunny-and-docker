package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Entity;
import java.io.IOException;
import java.nio.ByteBuffer;

public class NamedGroup implements Entity {

    public static final int ENCODING_LENGTH = 2;

    public static final Secp    secp256r1    = new Secp(0x0017, "secp256r1");
    public static final Secp    secp384r1    = new Secp(0x0018, "secp384r1");
    public static final Secp    secp521r1    = new Secp(0x0019, "secp521r1");
    public static final X       x25519       = new X(0x001D);
    public static final X       x448         = new X(0x001E);
    public static final FFDHE   ffdhe2048    = new FFDHE(0x0100);
    public static final FFDHE   ffdhe3072    = new FFDHE(0x0101);
    public static final FFDHE   ffdhe4096    = new FFDHE(0x0102);
    public static final FFDHE   ffdhe6144    = new FFDHE(0x0103);
    public static final FFDHE   ffdhe8192    = new FFDHE(0x0104);

    public final int code;

    private NamedGroup(int code) {
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
        
        if (obj instanceof NamedGroup == false) {
            return false;
        }
        
        NamedGroup other = (NamedGroup) obj;
        return this.code == other.code;
    }

    public static NamedGroup parse(ByteBuffer buffer) {
        return new NamedGroup(buffer.getShort());
    }
    
    public static class Secp extends NamedGroup {

        private final String curve;
        
        public Secp(int code, String curve) {
            super(code);
            this.curve = curve;
        }

        public String getCurve() {
            return curve;
        }

    }

    public static class X extends NamedGroup {

        public X(int code) {
            super(code);
        }

    }

    public static class FFDHE extends NamedGroup {

        public FFDHE(int code) {
            super(code);
        }

    }

}
