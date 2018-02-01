package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Bytes;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;
import java.util.Arrays;
import com.gypsyengineer.tlsbunny.tls13.struct.ContentType;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSInnerPlaintext;

public class TLSInnerPlaintextImpl implements TLSInnerPlaintext {

    private Bytes content;
    private ContentType type;
    private Bytes zeros;

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
    public void setContent(Bytes content) {
        this.content = content;
    }

    @Override
    public ContentType getType() {
        return type;
    }

    @Override
    public void setType(ContentType type) {
        this.type = type;
    }

    @Override
    public Bytes getZeros() {
        return zeros;
    }

    @Override
    public void setZeros(Bytes zeros) {
        this.zeros = zeros;
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

    public static TLSInnerPlaintextImpl parse(byte[] bytes) {
        int i = bytes.length - 1;
        while (i > 0) {
            if (bytes[i] != 0) {
                break;
            }
            
            i--;
        }

        if (i == 0) {
            throw new IllegalArgumentException();
        }

        byte[] zeros = new byte[bytes.length - 1 - i];
        byte[] content = Arrays.copyOfRange(bytes, 0, i);
        
        return new TLSInnerPlaintextImpl(
                new Bytes(content), 
                new ContentTypeImpl(bytes[i]), 
                new Bytes(zeros));
    }
}
