/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls.UInt16;
import com.gypsyengineer.tlsbunny.tls.Vector;
import java.io.IOException;

/**
 *
 * @author artem
 */
public interface HkdfLabel extends Struct {

    int HASH_VALUE_LENGTH_BYTES = 1;
    int LABEL_LENGTH_BYTES = 1;
    int MAX_HASH_VALUE_LENGTH = 255;
    int MAX_LABEL_LENGTH = 255;
    int MIN_HASH_VALUE_LENGTH = 0;
    int MIN_LABEL_LENGTH = 7;

    byte[] encoding() throws IOException;

    int encodingLength();

    Vector<Byte> getHashValue();

    Vector<Byte> getLabel();

    UInt16 getLength();

    void setHashValue(Vector<Byte> hash_value);

    void setLabel(Vector<Byte> label);

    void setLength(UInt16 length);
    
}
