/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.ContentTypeImpl;
import com.gypsyengineer.tlsbunny.tls.Bytes;
import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;

/**
 *
 * @author artem
 */
public interface TLSInnerPlaintext extends Struct {

    byte[] NO_PADDING = Utils.EMPTY_ARRAY;

    boolean containsAlert();

    boolean containsApplicationData();

    boolean containsHandshake();

    byte[] encoding() throws IOException;

    int encodingLength();

    byte[] getContent();

    ContentTypeImpl getType();

    Bytes getZeros();

    void setContent(Bytes content);

    void setType(ContentTypeImpl type);

    void setZeros(Bytes zeros);
    
}
