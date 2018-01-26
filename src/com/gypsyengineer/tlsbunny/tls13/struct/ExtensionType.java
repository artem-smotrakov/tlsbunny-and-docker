package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Entity;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ExtensionType implements Entity {

    public static final int ENCODING_LENGTH = 2;
    
    public static final ExtensionType server_name = new ExtensionType(0);
    public static final ExtensionType max_fragment_length = new ExtensionType(1);
    public static final ExtensionType status_request = new ExtensionType(5);
    public static final ExtensionType supported_groups = new ExtensionType(10);
    public static final ExtensionType signature_algorithms = new ExtensionType(13);
    public static final ExtensionType use_srtp = new ExtensionType(14);
    public static final ExtensionType heartbeat = new ExtensionType(15);
    public static final ExtensionType application_layer_protocol_negotiation = new ExtensionType(16);
    public static final ExtensionType signed_certificate_timestamp = new ExtensionType(18);
    public static final ExtensionType client_certificate_type = new ExtensionType(19);
    public static final ExtensionType server_certificate_type = new ExtensionType(20);
    public static final ExtensionType padding = new ExtensionType(21);
    public static final ExtensionType key_share = new ExtensionType(40);
    public static final ExtensionType pre_shared_key = new ExtensionType(41);
    public static final ExtensionType early_data = new ExtensionType(42);
    public static final ExtensionType supported_versions = new ExtensionType(43);
    public static final ExtensionType cookie = new ExtensionType(44);
    public static final ExtensionType psk_key_exchange_modes = new ExtensionType(45);
    public static final ExtensionType certificate_authorities = new ExtensionType(47);
    public static final ExtensionType oid_filters = new ExtensionType(48);
    public static final ExtensionType post_handshake_auth = new ExtensionType(49);

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
