package com.gypsyengineer.tlsbunny.tls13;

import com.gypsyengineer.tlsbunny.tls.Entity;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ExtensionType implements Entity {

    public static final int ENCODING_LENGTH = 2;
    
    public static final ExtensionType SERVER_NAME = new ExtensionType(0);
    public static final ExtensionType MAX_FRAGMENT_LENGTH = new ExtensionType(1);
    public static final ExtensionType STATUS_REQUEST = new ExtensionType(5);
    public static final ExtensionType SUPPORTED_GROUPS = new ExtensionType(10);
    public static final ExtensionType SIGNATURE_ALGORITHMS = new ExtensionType(13);
    public static final ExtensionType USE_SRTP = new ExtensionType(14);
    public static final ExtensionType HEARTBEAT = new ExtensionType(15);
    public static final ExtensionType APPLICATION_LAYER_PROTOCOL_NEGOTIATION = new ExtensionType(16);
    public static final ExtensionType SIGNED_CERTIFICATE_TIMESTAMP = new ExtensionType(18);
    public static final ExtensionType CLIENT_CERTIFICATE_TYPE = new ExtensionType(19);
    public static final ExtensionType SERVER_CERTIFICATE_TYPE = new ExtensionType(20);
    public static final ExtensionType PADDING = new ExtensionType(21);
    public static final ExtensionType KEY_SHARE = new ExtensionType(40);
    public static final ExtensionType PRE_SHARED_KEY = new ExtensionType(41);
    public static final ExtensionType EARLY_DATA = new ExtensionType(42);
    public static final ExtensionType SUPPORTED_VERSIONS = new ExtensionType(43);
    public static final ExtensionType COOKIE = new ExtensionType(44);
    public static final ExtensionType PSK_KEY_EXCHANGE_MODES = new ExtensionType(45);
    public static final ExtensionType CERTIFICATE_AUTHORITIES = new ExtensionType(47);
    public static final ExtensionType OID_FILTERS = new ExtensionType(48);
    public static final ExtensionType POST_HANDSHAKE_AUTH = new ExtensionType(49);

    private int code;

    public ExtensionType(int code) {
        check(code);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        check(code);
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
        hash = 29 * hash + this.code;
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
        final ExtensionType other = (ExtensionType) obj;
        return this.code == other.code;
    }

    public static ExtensionType parse(ByteBuffer buffer) {
        return new ExtensionType(buffer.getShort());
    }
    
    private static void check(int code) {
        if (code < 0 || code > 65535) {
            throw new IllegalArgumentException();
        }
    }

}
