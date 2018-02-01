package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.ContentTypeImpl;
import com.gypsyengineer.tlsbunny.tls.Struct;

public interface ContentType extends Struct {

    int ENCODING_LENGTH = 1;

    ContentType alert = new ContentTypeImpl(21);
    ContentType application_data = new ContentTypeImpl(23);
    ContentType handshake = new ContentTypeImpl(22);
    ContentType invalid = new ContentTypeImpl(0);

    int getCode();
    boolean isAlert();
    boolean isApplicationData();
    boolean isHandshake();
    void setCode(int code);
}
