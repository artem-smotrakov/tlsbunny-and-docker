package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import java.nio.ByteBuffer;
import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls13.struct.AlertDescription;

public class AlertDescriptionImpl implements AlertDescription {


    public int code;

    public AlertDescriptionImpl(int code) {
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

    @Override
    public byte getCode() {
        return (byte) code;
    }
    
    @Override
    public void setCode(int code) {
        check(code);
        this.code = code;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.code;
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
        final AlertDescriptionImpl other = (AlertDescriptionImpl) obj;
        return this.code == other.code;
    }

    public static AlertDescriptionImpl parse(ByteBuffer data) {
        return new AlertDescriptionImpl(data.get() & 0xFF);
    }

    private static void check(int code) {
        if (code < MIN || code > MAX) {
            throw new IllegalArgumentException();
        }
    }

}
