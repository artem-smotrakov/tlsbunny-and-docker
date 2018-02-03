package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls13.struct.AlertDescription;
import java.util.HashMap;
import java.util.Map;

public class AlertDescriptionImpl implements AlertDescription {

    private final int code;

    AlertDescriptionImpl(int code) {
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

    @Override
    public String toString() {
        String string = NAMES.get((byte) code);
        if (string == null) {
            return "unknown";
        }

        return string;
    }

    private static final Map<Byte, String> NAMES = new HashMap<>();
    static {
        NAMES.put(access_denied.getCode(), "access_denied");
        NAMES.put(bad_certificate.getCode(), "bad_certificate");
        NAMES.put(bad_certificate_hash_value.getCode(), "bad_certificate_hash_value");
        NAMES.put(bad_certificate_status_response.getCode(), "bad_certificate_status_response");
        NAMES.put(bad_record_mac.getCode(), "bad_record_mac");
        NAMES.put(certificate_expired.getCode(), "certificate_expired");
        NAMES.put(certificate_required.getCode(), "certificate_required");
        NAMES.put(certificate_revoked.getCode(), "certificate_revoked");
        NAMES.put(certificate_unknown.getCode(), "certificate_unknown");
        NAMES.put(certificate_unobtainable.getCode(), "certificate_unobtainable");
        NAMES.put(close_notify.getCode(), "close_notify");
        NAMES.put(decode_error.getCode(), "decode_error");
        NAMES.put(decrypt_error.getCode(), "decrypt_error");
        NAMES.put(handshake_failure.getCode(), "handshake_failure");
        NAMES.put(illegal_parameter.getCode(), "illegal_parameter");
        NAMES.put(inapproptiate_fallback.getCode(), "inapproptiate_fallback");
        NAMES.put(insufficient_security.getCode(), "insufficient_security");
        NAMES.put(internal_error.getCode(), "internal_error");
        NAMES.put(missing_extension.getCode(), "missing_extension");
        NAMES.put(no_application_protocol.getCode(), "no_application_protocol");
        NAMES.put(protocol_version.getCode(), "protocol_version");
        NAMES.put(record_overflow.getCode(), "record_overflow");
        NAMES.put(unexpected_message.getCode(), "unexpected_message");
        NAMES.put(unknown_ca.getCode(), "unknown_ca");
        NAMES.put(unknown_psk_identity.getCode(), "");
        NAMES.put(unrecognized_name.getCode(), "unknown_psk_identity");
        NAMES.put(unsupported_certificate.getCode(), "unsupported_certificate");
        NAMES.put(unsupported_extension.getCode(), "unsupported_extension");
        NAMES.put(user_cancelled.getCode(), "user_cancelled");
    }

    private static void check(int code) {
        if (code < MIN || code > MAX) {
            throw new IllegalArgumentException();
        }
    }

}
