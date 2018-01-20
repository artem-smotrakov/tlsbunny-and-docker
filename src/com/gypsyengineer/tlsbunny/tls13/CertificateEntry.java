package com.gypsyengineer.tlsbunny.tls13;

import com.gypsyengineer.tlsbunny.tls.Entity;
import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class CertificateEntry implements Entity {

    public static final int EXTENSIONS_LENGTH_BYTES = 2;

    Vector<Extension> extensions;

    CertificateEntry(Vector<Extension> extensions) {
        this.extensions = extensions;
    }

    public Vector<Extension> getExtensions() {
        return extensions;
    }

    public void setExtensions(Vector<Extension> extensions) {
        this.extensions = extensions;
    }

    public void addExtension(Extension extension) {
        extensions.add(extension);
    }

    public void clearExtensions() {
        extensions.clear();
    }

    public Extension getExtension(ExtensionType type) {
        for (Extension extension : extensions.toList()) {
            if (type.equals(extension.getExtensionType())) {
                return extension;
            }
        }

        return null;
    }
    
    public static class X509 extends CertificateEntry {
    
        public static final int LENGTH_BYTES = 3;
        
        private Vector<Byte> cert_data;
        
        public X509(Vector<Byte> cert_data, Vector<Extension> extensions) {
            super(extensions);
            this.cert_data = cert_data;
        }

        public Vector<Byte> getCertData() {
            return cert_data;
        }

        public void setCertData(Vector<Byte> cert_data) {
            this.cert_data = cert_data;
        }
        
        @Override
        public int encodingLength() {
            return Utils.getEncodingLength(cert_data, extensions);
        }

        @Override
        public byte[] encoding() throws IOException {
            return Utils.encoding(cert_data, extensions);
        }       

        public static X509 parse(ByteBuffer buffer) {
            return new X509(
                    Vector.parseOpaqueVector(buffer, LENGTH_BYTES), 
                    Vector.parse(
                        buffer,
                        EXTENSIONS_LENGTH_BYTES,
                        buf -> Extension.parse(buf)));
        }

    }

    public static class RawPublicKey extends CertificateEntry {
    
        public static final int LENGTH_BYTES = 3;
        
        private Vector<Byte> ASN1_subjectPublicKeyInfo;
        
        public RawPublicKey(Vector<Byte> ASN1_subjectPublicKeyInfo, 
                Vector<Extension> extensions) {
            
            super(extensions);
            this.ASN1_subjectPublicKeyInfo = ASN1_subjectPublicKeyInfo;
        }

        public Vector<Byte> getASN1SubjectPublicKeyInfo() {
            return ASN1_subjectPublicKeyInfo;
        }

        public void setASN1SubjectPublicKeyInfo(Vector<Byte> ASN1_subjectPublicKeyInfo) {
            this.ASN1_subjectPublicKeyInfo = ASN1_subjectPublicKeyInfo;
        }
        
        @Override
        public int encodingLength() {
            return Utils.getEncodingLength(ASN1_subjectPublicKeyInfo, extensions);
        }

        @Override
        public byte[] encoding() throws IOException {
            return Utils.encoding(ASN1_subjectPublicKeyInfo, extensions);
        }       

        public static RawPublicKey parse(ByteBuffer buffer) {
            return new RawPublicKey(
                    Vector.parseOpaqueVector(buffer, LENGTH_BYTES), 
                    Vector.parse(
                        buffer,
                        EXTENSIONS_LENGTH_BYTES,
                        buf -> Extension.parse(buf)));
        }

    }

}
