/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.HandshakeTypeImpl;
import com.gypsyengineer.tlsbunny.tls.Bytes;
import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls.UInt24;
import java.io.IOException;

/**
 *
 * @author artem
 */
public interface Handshake extends Struct {

    boolean containsCertificate();

    boolean containsCertificateRequest();

    boolean containsCertificateVerify();

    boolean containsClientHello();

    boolean containsEncryptedExtensions();

    boolean containsFinished();

    boolean containsHelloRetryRequest();

    boolean containsNewSessionTicket();

    boolean containsServerHello();

    byte[] encoding() throws IOException;

    int encodingLength();

    byte[] getBody();

    UInt24 getLength();

    HandshakeTypeImpl getMessageType();

    void setBody(Bytes body);

    void setLength(UInt24 length);

    void setMessageType(HandshakeTypeImpl msg_type);
    
}
