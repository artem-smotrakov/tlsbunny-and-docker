package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Vector;
import java.io.IOException;
import java.nio.ByteBuffer;
import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls13.struct.SignatureSchemeList;

public class SignatureSchemeListImpl implements SignatureSchemeList {


    private Vector<SignatureSchemeImpl> supported_signature_algorithms;

    public SignatureSchemeListImpl(Vector<SignatureSchemeImpl> supported_signature_algorithms) {
        this.supported_signature_algorithms = supported_signature_algorithms;
    }

    @Override
    public void set(SignatureSchemeImpl signatureScheme) {
        supported_signature_algorithms = Vector.wrap(LENGTH_BYTES, signatureScheme);
    }

    @Override
    public Vector<SignatureSchemeImpl> getSupportedSignatureAlgorithms() {
        return supported_signature_algorithms;
    }

    @Override
    public void setSupportedSignatureAlgorithms(
            Vector<SignatureSchemeImpl> supported_signature_algorithms) {

        this.supported_signature_algorithms = supported_signature_algorithms;
    }
    
    @Override
    public int encodingLength() {
        return supported_signature_algorithms.encodingLength();
    }

    @Override
    public byte[] encoding() throws IOException {
        return supported_signature_algorithms.encoding();
    }

    public static SignatureSchemeListImpl create(SignatureSchemeImpl... signatureSchemes) {
        return new SignatureSchemeListImpl(Vector.wrap(LENGTH_BYTES, signatureSchemes));
    }

    public static SignatureSchemeListImpl parse(ByteBuffer buffer) {
        return new SignatureSchemeListImpl(
                Vector.parse(buffer, LENGTH_BYTES, buf -> SignatureSchemeImpl.parse(buf)));
    }
    
    public static SignatureSchemeListImpl parse(Vector<Byte> extenstion_data) 
            throws IOException {
        
        return parse(ByteBuffer.wrap(extenstion_data.bytes()));
    }

}
