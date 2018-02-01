package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.AlertDescriptionImpl;
import com.gypsyengineer.tlsbunny.tls.Struct;

public interface AlertDescription extends Struct {

    int ENCODING_LENGTH = 1;
    int MAX = 255;
    int MIN = 0;

    AlertDescription ACCESS_DENIED = new AlertDescriptionImpl(49);
    AlertDescription BAD_CERTIFICATE = new AlertDescriptionImpl(42);
    AlertDescription BAD_CERTIFICATE_HASH_VALUE = new AlertDescriptionImpl(114);
    AlertDescription BAD_CERTIFICATE_STATUS_RESPONSE = new AlertDescriptionImpl(113);
    AlertDescription BAD_RECORD_MAC = new AlertDescriptionImpl(20);
    AlertDescription CERTIFICATE_EXPIRED = new AlertDescriptionImpl(45);
    AlertDescription CERTIFICATE_REQUIRED = new AlertDescriptionImpl(116);
    AlertDescription CERTIFICATE_REVOKED = new AlertDescriptionImpl(44);
    AlertDescription CERTIFICATE_UNKNOWN = new AlertDescriptionImpl(46);
    AlertDescription CERTIFICATE_UNOBTAINABLE = new AlertDescriptionImpl(111);
    AlertDescription CLOSE_NOTIFY = new AlertDescriptionImpl(0);
    AlertDescription DECODE_ERROR = new AlertDescriptionImpl(50);
    AlertDescription DECRYPT_ERROR = new AlertDescriptionImpl(51);
    AlertDescription HANDSHAKE_FAILURE = new AlertDescriptionImpl(40);
    AlertDescription ILLEGAL_PARAMETER = new AlertDescriptionImpl(47);
    AlertDescription INAPPROPRIATE_FALLBACK = new AlertDescriptionImpl(86);
    AlertDescription INSUFFICIENT_SECURITY = new AlertDescriptionImpl(71);
    AlertDescription INTERNAL_ERROR = new AlertDescriptionImpl(80);
    AlertDescription MISSING_EXTENSION = new AlertDescriptionImpl(109);
    AlertDescription NO_APPLICATION_PROTOCOL = new AlertDescriptionImpl(120);
    AlertDescription PROTOCOL_VERSION = new AlertDescriptionImpl(70);
    AlertDescription RECORD_OVERFLOW = new AlertDescriptionImpl(22);
    AlertDescription UNEXPECTED_MESSAGE = new AlertDescriptionImpl(10);
    AlertDescription UNKNOWN_CA = new AlertDescriptionImpl(48);
    AlertDescription UNKNOWN_PSK_IDENTITY = new AlertDescriptionImpl(115);
    AlertDescription UNRECOGNIZED_NAME = new AlertDescriptionImpl(112);
    AlertDescription UNSUPPORTED_CERTIFICATE = new AlertDescriptionImpl(43);
    AlertDescription UNSUPPORTED_EXTENSION = new AlertDescriptionImpl(110);
    AlertDescription USER_CANCELLED = new AlertDescriptionImpl(90);

    byte getCode();
    void setCode(int code);
}
