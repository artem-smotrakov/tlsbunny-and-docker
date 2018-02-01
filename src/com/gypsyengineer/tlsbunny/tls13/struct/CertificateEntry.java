/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.ExtensionTypeImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.ExtensionImpl;
import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls.Vector;

/**
 *
 * @author artem
 */
public interface CertificateEntry extends Struct {

    int EXTENSIONS_LENGTH_BYTES = 2;

    void addExtension(ExtensionImpl extension);

    void clearExtensions();

    ExtensionImpl getExtension(ExtensionTypeImpl type);

    Vector<ExtensionImpl> getExtensions();

    void setExtensions(Vector<ExtensionImpl> extensions);
    
}
