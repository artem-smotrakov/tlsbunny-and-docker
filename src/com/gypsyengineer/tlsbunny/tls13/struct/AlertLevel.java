package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Struct;

public interface AlertLevel extends Struct {

    int ENCODING_LENGTH = 1;
    int MAX = 255;
    int MIN = 0;
    
    AlertLevel fatal = StructFactory.getDefault().createAlertLevel(2);
    AlertLevel warning = StructFactory.getDefault().createAlertLevel(1);

    byte getCode();
}
