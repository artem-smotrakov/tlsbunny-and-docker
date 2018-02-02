package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Struct;

public interface ContentType extends Struct {

    int ENCODING_LENGTH = 1;

    ContentType alert = StructFactory.getDefault().createContentType(21);
    ContentType application_data = StructFactory.getDefault().createContentType(23);
    ContentType handshake = StructFactory.getDefault().createContentType(22);
    ContentType invalid = StructFactory.getDefault().createContentType(0);

    int getCode();
    boolean isAlert();
    boolean isApplicationData();
    boolean isHandshake();
}
