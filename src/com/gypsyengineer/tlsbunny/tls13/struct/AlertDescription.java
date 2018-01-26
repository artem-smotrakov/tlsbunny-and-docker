package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Entity;
import java.nio.ByteBuffer;

public class AlertDescription implements Entity {

    public static final int ENCODING_LENGTH = 1;
    public static final int MIN = 0;
    public static final int MAX = 255;
    
    public static final AlertDescription CLOSE_NOTIFY = new AlertDescription(0);
    public static final AlertDescription UNEXPECTED_MESSAGE = new AlertDescription(10);
    public static final AlertDescription BAD_RECORD_MAC = new AlertDescription(20);
    public static final AlertDescription RECORD_OVERFLOW = new AlertDescription(22);
    public static final AlertDescription HANDSHAKE_FAILURE = new AlertDescription(40);
    public static final AlertDescription BAD_CERTIFICATE = new AlertDescription(42);
    public static final AlertDescription UNSUPPORTED_CERTIFICATE = new AlertDescription(43);
    public static final AlertDescription CERTIFICATE_REVOKED = new AlertDescription(44);
    public static final AlertDescription CERTIFICATE_EXPIRED = new AlertDescription(45);
    public static final AlertDescription CERTIFICATE_UNKNOWN = new AlertDescription(46);
    public static final AlertDescription ILLEGAL_PARAMETER = new AlertDescription(47);
    public static final AlertDescription UNKNOWN_CA = new AlertDescription(48);
    public static final AlertDescription ACCESS_DENIED = new AlertDescription(49);
    public static final AlertDescription DECODE_ERROR = new AlertDescription(50);
    public static final AlertDescription DECRYPT_ERROR = new AlertDescription(51);
    public static final AlertDescription PROTOCOL_VERSION = new AlertDescription(70);
    public static final AlertDescription INSUFFICIENT_SECURITY = new AlertDescription(71);
    public static final AlertDescription INTERNAL_ERROR = new AlertDescription(80);
    public static final AlertDescription INAPPROPRIATE_FALLBACK = new AlertDescription(86);
    public static final AlertDescription USER_CANCELLED = new AlertDescription(90);
    public static final AlertDescription MISSING_EXTENSION = new AlertDescription(109);
    public static final AlertDescription UNSUPPORTED_EXTENSION = new AlertDescription(110);
    public static final AlertDescription CERTIFICATE_UNOBTAINABLE = new AlertDescription(111);
    public static final AlertDescription UNRECOGNIZED_NAME = new AlertDescription(112);
    public static final AlertDescription BAD_CERTIFICATE_STATUS_RESPONSE = new AlertDescription(113);
    public static final AlertDescription BAD_CERTIFICATE_HASH_VALUE = new AlertDescription(114);
    public static final AlertDescription UNKNOWN_PSK_IDENTITY = new AlertDescription(115);
    public static final AlertDescription CERTIFICATE_REQUIRED = new AlertDescription(116);
    public static final AlertDescription NO_APPLICATION_PROTOCOL = new AlertDescription(120);

    public int code;

    public AlertDescription(int code) {
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
        final AlertDescription other = (AlertDescription) obj;
        return this.code == other.code;
    }

    public static AlertDescription parse(ByteBuffer data) {
        return new AlertDescription(data.get() & 0xFF);
    }

    private static void check(int code) {
        if (code < MIN || code > MAX) {
            throw new IllegalArgumentException();
        }
    }

}
