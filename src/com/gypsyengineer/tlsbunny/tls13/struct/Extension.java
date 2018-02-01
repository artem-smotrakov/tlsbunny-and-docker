/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.ExtensionTypeImpl;
import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls.Vector;
import java.io.IOException;

/**
 *
 * @author artem
 */
public interface Extension extends Struct {

    int EXTENSION_DATA_LENGTH_BYTES = 2;

    byte[] encoding() throws IOException;

    int encodingLength();

    Vector<Byte> getExtensionData();

    ExtensionTypeImpl getExtensionType();

    void setExtensionData(Vector<Byte> extension_data);

    void setExtensionType(ExtensionTypeImpl extension_type);
    
}
