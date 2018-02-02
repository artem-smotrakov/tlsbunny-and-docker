package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Struct;

public interface AlertDescription extends Struct {

    int ENCODING_LENGTH = 1;
    int MAX = 255;
    int MIN = 0;

    AlertDescription ACCESS_DENIED = StructFactory.getDefault().createAlertDescription(49);
    AlertDescription BAD_CERTIFICATE = StructFactory.getDefault().createAlertDescription(42);
    AlertDescription BAD_CERTIFICATE_HASH_VALUE = StructFactory.getDefault().createAlertDescription(114);
    AlertDescription BAD_CERTIFICATE_STATUS_RESPONSE = StructFactory.getDefault().createAlertDescription(113);
    AlertDescription BAD_RECORD_MAC = StructFactory.getDefault().createAlertDescription(20);
    AlertDescription CERTIFICATE_EXPIRED = StructFactory.getDefault().createAlertDescription(45);
    AlertDescription CERTIFICATE_REQUIRED = StructFactory.getDefault().createAlertDescription(116);
    AlertDescription CERTIFICATE_REVOKED = StructFactory.getDefault().createAlertDescription(44);
    AlertDescription CERTIFICATE_UNKNOWN = StructFactory.getDefault().createAlertDescription(46);
    AlertDescription CERTIFICATE_UNOBTAINABLE = StructFactory.getDefault().createAlertDescription(111);
    AlertDescription CLOSE_NOTIFY = StructFactory.getDefault().createAlertDescription(0);
    AlertDescription DECODE_ERROR = StructFactory.getDefault().createAlertDescription(50);
    AlertDescription DECRYPT_ERROR = StructFactory.getDefault().createAlertDescription(51);
    AlertDescription HANDSHAKE_FAILURE = StructFactory.getDefault().createAlertDescription(40);
    AlertDescription ILLEGAL_PARAMETER = StructFactory.getDefault().createAlertDescription(47);
    AlertDescription INAPPROPRIATE_FALLBACK = StructFactory.getDefault().createAlertDescription(86);
    AlertDescription INSUFFICIENT_SECURITY = StructFactory.getDefault().createAlertDescription(71);
    AlertDescription INTERNAL_ERROR = StructFactory.getDefault().createAlertDescription(80);
    AlertDescription MISSING_EXTENSION = StructFactory.getDefault().createAlertDescription(109);
    AlertDescription NO_APPLICATION_PROTOCOL = StructFactory.getDefault().createAlertDescription(120);
    AlertDescription PROTOCOL_VERSION = StructFactory.getDefault().createAlertDescription(70);
    AlertDescription RECORD_OVERFLOW = StructFactory.getDefault().createAlertDescription(22);
    AlertDescription UNEXPECTED_MESSAGE = StructFactory.getDefault().createAlertDescription(10);
    AlertDescription UNKNOWN_CA = StructFactory.getDefault().createAlertDescription(48);
    AlertDescription UNKNOWN_PSK_IDENTITY = StructFactory.getDefault().createAlertDescription(115);
    AlertDescription UNRECOGNIZED_NAME = StructFactory.getDefault().createAlertDescription(112);
    AlertDescription UNSUPPORTED_CERTIFICATE = StructFactory.getDefault().createAlertDescription(43);
    AlertDescription UNSUPPORTED_EXTENSION = StructFactory.getDefault().createAlertDescription(110);
    AlertDescription USER_CANCELLED = StructFactory.getDefault().createAlertDescription(90);

    byte getCode();
    void setCode(int code);
}
