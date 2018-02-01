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
public interface UncompressedPointRepresentation extends Struct {

    byte[] encoding();

    int encodingLength();

    byte[] getX();

    byte[] getY();
    
}
