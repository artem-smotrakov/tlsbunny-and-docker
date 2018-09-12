package com.gypsyengineer.tlsbunny.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CertificateHolder {

    public static final CertificateHolder NO_CERTIFICATE = null;
    
    private final byte[] cert_data;
    private final byte[] key_data;    

    public CertificateHolder(byte[] cert_data, byte[] key_data) {
        this.cert_data = cert_data.clone();
        this.key_data = key_data.clone();
    }
    
    public byte[] getCertData() {
        return cert_data.clone();
    }
    
    public byte[] getKeyData() {
        return key_data.clone();
    }
    
    public static CertificateHolder load(String pathToCert, String pathToKey) 
            throws IOException {
        
        return new CertificateHolder(
                Files.readAllBytes(Paths.get(pathToCert)),
                Files.readAllBytes(Paths.get(pathToKey)));
    }
}
