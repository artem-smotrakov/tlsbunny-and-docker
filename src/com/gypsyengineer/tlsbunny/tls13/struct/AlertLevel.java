package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.AlertLevelImpl;
import com.gypsyengineer.tlsbunny.tls.Struct;

public interface AlertLevel extends Struct {

    int ENCODING_LENGTH = 1;
    int MAX = 255;
    int MIN = 0;
    
    AlertLevel FATAL = new AlertLevelImpl(2);
    AlertLevel WARNING = new AlertLevelImpl(1);

    byte getCode();
    void setCode(int code);
}
