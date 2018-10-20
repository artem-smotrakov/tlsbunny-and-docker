package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Struct;

public interface ChangeCipherSpec extends Struct {

    int ENCODING_LENGTH = 1;
    int VALID_VALUE = 1;
    int MIN = 0;
    int MAX = 255;

    int getValue();
    boolean isValid();
}
