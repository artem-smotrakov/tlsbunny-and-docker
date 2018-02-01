/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.AlertDescriptionImpl;
import com.gypsyengineer.tlsbunny.tls.Struct;

/**
 *
 * @author artem
 */
public interface AlertDescription extends Struct {

    AlertDescriptionImpl ACCESS_DENIED = new AlertDescriptionImpl(49);
    AlertDescriptionImpl BAD_CERTIFICATE = new AlertDescriptionImpl(42);
    AlertDescriptionImpl BAD_CERTIFICATE_HASH_VALUE = new AlertDescriptionImpl(114);
    AlertDescriptionImpl BAD_CERTIFICATE_STATUS_RESPONSE = new AlertDescriptionImpl(113);
    AlertDescriptionImpl BAD_RECORD_MAC = new AlertDescriptionImpl(20);
    AlertDescriptionImpl CERTIFICATE_EXPIRED = new AlertDescriptionImpl(45);
    AlertDescriptionImpl CERTIFICATE_REQUIRED = new AlertDescriptionImpl(116);
    AlertDescriptionImpl CERTIFICATE_REVOKED = new AlertDescriptionImpl(44);
    AlertDescriptionImpl CERTIFICATE_UNKNOWN = new AlertDescriptionImpl(46);
    AlertDescriptionImpl CERTIFICATE_UNOBTAINABLE = new AlertDescriptionImpl(111);
    AlertDescriptionImpl CLOSE_NOTIFY = new AlertDescriptionImpl(0);
    AlertDescriptionImpl DECODE_ERROR = new AlertDescriptionImpl(50);
    AlertDescriptionImpl DECRYPT_ERROR = new AlertDescriptionImpl(51);
    int ENCODING_LENGTH = 1;
    AlertDescriptionImpl HANDSHAKE_FAILURE = new AlertDescriptionImpl(40);
    AlertDescriptionImpl ILLEGAL_PARAMETER = new AlertDescriptionImpl(47);
    AlertDescriptionImpl INAPPROPRIATE_FALLBACK = new AlertDescriptionImpl(86);
    AlertDescriptionImpl INSUFFICIENT_SECURITY = new AlertDescriptionImpl(71);
    AlertDescriptionImpl INTERNAL_ERROR = new AlertDescriptionImpl(80);
    int MAX = 255;
    int MIN = 0;
    AlertDescriptionImpl MISSING_EXTENSION = new AlertDescriptionImpl(109);
    AlertDescriptionImpl NO_APPLICATION_PROTOCOL = new AlertDescriptionImpl(120);
    AlertDescriptionImpl PROTOCOL_VERSION = new AlertDescriptionImpl(70);
    AlertDescriptionImpl RECORD_OVERFLOW = new AlertDescriptionImpl(22);
    AlertDescriptionImpl UNEXPECTED_MESSAGE = new AlertDescriptionImpl(10);
    AlertDescriptionImpl UNKNOWN_CA = new AlertDescriptionImpl(48);
    AlertDescriptionImpl UNKNOWN_PSK_IDENTITY = new AlertDescriptionImpl(115);
    AlertDescriptionImpl UNRECOGNIZED_NAME = new AlertDescriptionImpl(112);
    AlertDescriptionImpl UNSUPPORTED_CERTIFICATE = new AlertDescriptionImpl(43);
    AlertDescriptionImpl UNSUPPORTED_EXTENSION = new AlertDescriptionImpl(110);
    AlertDescriptionImpl USER_CANCELLED = new AlertDescriptionImpl(90);

    byte[] encoding();

    int encodingLength();

    boolean equals(Object obj);

    byte getCode();

    int hashCode();

    void setCode(int code);
    
}
