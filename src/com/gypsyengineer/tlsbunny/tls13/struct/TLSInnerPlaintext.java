package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Bytes;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;
import java.util.Arrays;
import com.gypsyengineer.tlsbunny.tls.Struct;

public class TLSInnerPlaintext implements Struct {

    private Bytes content;
    private ContentType type;
    private Bytes zeros;

    public TLSInnerPlaintext(Bytes content, ContentType type, Bytes zeros) {
        this.content = content;
        this.type = type;
        this.zeros = zeros;
    }
    
    public byte[] getContent() {
        return content.encoding();
    }

    public void setContent(Bytes content) {
        this.content = content;
    }

    public ContentType getType() {
        return type;
    }

    public void setType(ContentType type) {
        this.type = type;
    }

    public Bytes getZeros() {
        return zeros;
    }

    public void setZeros(Bytes zeros) {
        this.zeros = zeros;
    }

    public boolean containsHandshake() {
        return type.isHandshake();
    }

    public boolean containsApplicationData() {
        return type.isApplicationData();
    }

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

    public static TLSInnerPlaintext noPadding(ContentType type, byte[] content) {
        return new TLSInnerPlaintext(new Bytes(content), type, Bytes.EMPTY);
    }

    public static TLSInnerPlaintext parse(byte[] bytes) {
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
        
        return new TLSInnerPlaintext(
                new Bytes(content), 
                new ContentType(bytes[i]), 
                new Bytes(zeros));
    }
}
