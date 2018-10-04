package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Bytes;
import com.gypsyengineer.tlsbunny.utils.Utils;
import com.gypsyengineer.tlsbunny.tls13.struct.ContentType;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSInnerPlaintext;
import java.io.IOException;

public class TLSInnerPlaintextImpl implements TLSInnerPlaintext {

    private final Bytes content;
    private final ContentType type;
    private final Bytes zeros;

    TLSInnerPlaintextImpl(Bytes content, ContentType type, Bytes zeros) {
        this.content = content;
        this.type = type;
        this.zeros = zeros;
    }
    
    @Override
    public byte[] getContent() {
        return content.encoding();
    }

    @Override
    public ContentType getType() {
        return type;
    }

    @Override
    public Bytes getZeros() {
        return zeros;
    }

    @Override
    public boolean containsHandshake() {
        return type.isHandshake();
    }

    @Override
    public boolean containsApplicationData() {
        return type.isApplicationData();
    }

    @Override
    public boolean containsAlert() {
        return type.isAlert();
    }
    
    @Override
    public int encodingLength() {
        return Utils.getEncodingLength(content, type, zeros);
    }

    @Override
    public byte[] encoding() throws IOException {
        return Utils.encoding(content, type, zeros);
    }

    @Override
    public TLSInnerPlaintextImpl copy() {
        return new TLSInnerPlaintextImpl(
                content.copy(), (ContentType) type.copy(), zeros.copy());
    }

}
