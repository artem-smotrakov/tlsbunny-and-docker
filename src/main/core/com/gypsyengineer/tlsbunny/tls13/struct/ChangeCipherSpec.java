package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Struct;

public interface ChangeCipherSpec extends Struct {

    int ENCODING_LENGTH = 1;
    int valid_value = 1;
    int min = 0;
    int max = 255;

    int getValue();
    boolean isValid();
}
