package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Struct;

public interface ChangeCipherSpec extends Struct {

    int ENCODING_LENGTH = 1;
    int VALID_VALUE = 0x01;

    int getValue();
    boolean isValid();
}
