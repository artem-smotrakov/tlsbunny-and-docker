/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Struct;

/**
 *
 * @author artem
 */
public interface CompressionMethod extends Struct {

    int ENCODING_LENGTH = 1;

    byte[] encoding();

    int encodingLength();

    int getCode();

    void setCode(int code);
    
}
