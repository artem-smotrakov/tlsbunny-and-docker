/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.ContentTypeImpl;
import com.gypsyengineer.tlsbunny.tls.Struct;

/**
 *
 * @author artem
 */
public interface ContentType extends Struct {

    int ENCODING_LENGTH = 1;
    ContentTypeImpl alert = new ContentTypeImpl(21);
    ContentTypeImpl application_data = new ContentTypeImpl(23);
    ContentTypeImpl handshake = new ContentTypeImpl(22);
    ContentTypeImpl invalid = new ContentTypeImpl(0);

    byte[] encoding();

    int encodingLength();

    boolean equals(Object obj);

    int getCode();

    int hashCode();

    boolean isAlert();

    boolean isApplicationData();

    boolean isHandshake();

    void setCode(int code);
    
}
