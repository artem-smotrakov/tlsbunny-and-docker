package com.gypsyengineer.tlsbunny.tls13.fuzzer;

import com.gypsyengineer.tlsbunny.tls.UInt16;
import com.gypsyengineer.tlsbunny.tls13.struct.ContentType;
import com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;
import java.io.IOException;

public class MutatedStruct implements TLSPlaintext {

    private final int mutatedEncodingLength;
    private final byte[] mutatedEncoding;

    public MutatedStruct(byte[] mutatedEncoding) {
        this(mutatedEncoding.length, mutatedEncoding);
    }
    
    public MutatedStruct(int mutatedEncodingLength, byte[] mutatedEncoding) {
        this.mutatedEncodingLength = mutatedEncodingLength;
        this.mutatedEncoding = mutatedEncoding;
    }
    
    // Struct
    
    @Override
    public int encodingLength() {
        return mutatedEncodingLength;
    }

    @Override
    public byte[] encoding() throws IOException {
        return mutatedEncoding;
    }
    
    // TLSPlaintext
    
    @Override
    public boolean containsAlert() {
        throw new UnsupportedOperationException("I don't know how to do that!");
    }

    @Override
    public boolean containsApplicationData() {
        throw new UnsupportedOperationException("I don't know how to do that!");
    }

    @Override
    public boolean containsHandshake() {
        throw new UnsupportedOperationException("I don't know how to do that!");
    }

    @Override
    public byte[] getFragment() {
        throw new UnsupportedOperationException("I don't know how to do that!");
    }

    @Override
    public ProtocolVersion getLegacyRecordVersion() {
        throw new UnsupportedOperationException("I don't know how to do that!");
    }

    @Override
    public UInt16 getLength() {
        throw new UnsupportedOperationException("I don't know how to do that!");
    }

    @Override
    public ContentType getType() {
        throw new UnsupportedOperationException("I don't know how to do that!");
    }
    
}
