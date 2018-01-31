package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Bytes;
import com.gypsyengineer.tlsbunny.tls.UInt16;
import com.gypsyengineer.tlsbunny.tls.UInt24;
import com.gypsyengineer.tlsbunny.utils.Utils;

public class StructFactory {
    
    public static StructFactory getDefault() {
        return new StructFactory();
    }

    public TLSPlaintext createTLSPlaintext(
            ContentType type, ProtocolVersion version, byte[] content) {
        
        return new TLSPlaintext(type, version, new UInt16(content.length), new Bytes(content));
    }
    
    public TLSPlaintext[] createTLSPlaintexts(
            ContentType type, ProtocolVersion version, byte[] content) {
        
        if (content.length <= TLSPlaintext.MAX_ALLOWED_LENGTH) {
            return new TLSPlaintext[] {
                createTLSPlaintext(type, version, content)
            };
        }

        byte[][] fragments = Utils.split(content, TLSPlaintext.MAX_ALLOWED_LENGTH);
        TLSPlaintext[] tlsPlaintexts = new TLSPlaintext[fragments.length];
        for (int i=0; i < fragments.length; i++) {
            tlsPlaintexts[i] = createTLSPlaintext(type, version, fragments[i]);
        }

        return tlsPlaintexts;
    }
    
    public TLSInnerPlaintext createTLSInnerPlaintext(ContentType type, byte[] content, byte[] zeros) {
        return new TLSInnerPlaintext(new Bytes(content), type, new Bytes(zeros));
    }
    
    public Handshake createHandshake(HandshakeType type, byte[] content) {
        return new Handshake(type, new UInt24(content.length), new Bytes(content));
    }
}
