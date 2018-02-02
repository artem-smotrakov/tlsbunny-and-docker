package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls.Vector;

public interface CertificateEntry extends Struct {

    int EXTENSIONS_LENGTH_BYTES = 2;

    Extension getExtension(ExtensionType type);
    Vector<Extension> getExtensions();
    
    public static interface X509 extends CertificateEntry {
    
        public static final int LENGTH_BYTES = 3;

        public Vector<Byte> getCertData();
    }

    public static interface RawPublicKey extends CertificateEntry {
    
        public static final int LENGTH_BYTES = 3;
        
        public Vector<Byte> getASN1SubjectPublicKeyInfo();
    }
}
