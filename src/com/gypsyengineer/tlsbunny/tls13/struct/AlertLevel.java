package com.gypsyengineer.tlsbunny.tls13.struct;

import java.nio.ByteBuffer;
import com.gypsyengineer.tlsbunny.tls.Struct;

public class AlertLevel implements Struct {

    public static final int ENCODING_LENGTH = 1;
    public static final int MIN = 0;
    public static final int MAX = 255;
    public static final AlertLevel WARNING = new AlertLevel(1);
    public static final AlertLevel FATAL = new AlertLevel(2);
    
    public int code;

    public AlertLevel(int code) {
        check(code);
        this.code = code;
    }

    @Override
    public int encodingLength() {
        return ENCODING_LENGTH;
    }

    @Override
    public byte[] encoding() {
        return new byte[] { (byte) code };
    }

    public byte getCode() {
        return (byte) code;
    }
    
    public void setCode(int code) {
        check(code);
        this.code = code;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.code;
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
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AlertLevel other = (AlertLevel) obj;
        return this.code == other.code;
    }

    public static AlertLevel parse(ByteBuffer data) {
        return new AlertLevel(data.get() & 0xFF);
    }

    private static void check(int code) {
        if (code < MIN || code > MAX) {
            throw new IllegalArgumentException();
        }
    }

}
