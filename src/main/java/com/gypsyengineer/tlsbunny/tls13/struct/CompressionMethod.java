package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Struct;

public interface CompressionMethod extends Struct {

    CompressionMethod None = StructFactory.getDefault().createCompressionMethod(0);

    int ENCODING_LENGTH = 1;

    int getCode();
}
