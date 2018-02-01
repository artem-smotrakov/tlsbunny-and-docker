/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.AlertDescriptionImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.AlertLevelImpl;
import com.gypsyengineer.tlsbunny.tls.Struct;
import java.io.IOException;

/**
 *
 * @author artem
 */
public interface Alert extends Struct {

    byte[] encoding() throws IOException;

    int encodingLength();

    boolean equals(Object obj);

    AlertDescriptionImpl getDescription();

    AlertLevelImpl getLevel();

    int hashCode();

    void setDescription(AlertDescriptionImpl description);

    void setLevel(AlertLevelImpl level);
    
}
