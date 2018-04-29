package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls.Vector;

public interface SignatureSchemeList extends Struct {

    int LENGTH_BYTES = 2;

    Vector<SignatureScheme> getSupportedSignatureAlgorithms();
}
