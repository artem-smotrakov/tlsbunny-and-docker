/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.AlertLevelImpl;
import com.gypsyengineer.tlsbunny.tls.Struct;

/**
 *
 * @author artem
 */
public interface AlertLevel extends Struct {

    int ENCODING_LENGTH = 1;
    AlertLevelImpl FATAL = new AlertLevelImpl(2);
    int MAX = 255;
    int MIN = 0;
    AlertLevelImpl WARNING = new AlertLevelImpl(1);

    byte[] encoding();

    int encodingLength();

    boolean equals(Object obj);

    byte getCode();

    int hashCode();

    void setCode(int code);
    
}
