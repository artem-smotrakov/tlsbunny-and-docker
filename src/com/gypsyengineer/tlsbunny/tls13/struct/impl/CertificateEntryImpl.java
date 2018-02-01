package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.ExtensionTypeImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.ExtensionImpl;
import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;
import java.nio.ByteBuffer;
import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls13.struct.CertificateEntry;

public abstract class CertificateEntryImpl implements CertificateEntry {


    Vector<ExtensionImpl> extensions;

    CertificateEntryImpl(Vector<ExtensionImpl> extensions) {
        this.extensions = extensions;
    }

    @Override
    public Vector<ExtensionImpl> getExtensions() {
        return extensions;
    }

    @Override
    public void setExtensions(Vector<ExtensionImpl> extensions) {
        this.extensions = extensions;
    }

    @Override
    public void addExtension(ExtensionImpl extension) {
        extensions.add(extension);
    }

    @Override
    public void clearExtensions() {
        extensions.clear();
    }

    @Override
    public ExtensionImpl getExtension(ExtensionTypeImpl type) {
        for (ExtensionImpl extension : extensions.toList()) {
            if (type.equals(extension.getExtensionType())) {
                return extension;
            }
        }

        return null;
    }
    
    public static class X509 extends CertificateEntryImpl {
    
        public static final int LENGTH_BYTES = 3;
        
        private Vector<Byte> cert_data;
        
        public X509(Vector<Byte> cert_data, Vector<ExtensionImpl> extensions) {
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
                        buf -> ExtensionImpl.parse(buf)));
        }
        
        public static X509 wrap(byte[] bytes) {
            return new X509(
                    Vector.wrap(LENGTH_BYTES, bytes), 
                    Vector.wrap(EXTENSIONS_LENGTH_BYTES));
        }

    }

    public static class RawPublicKey extends CertificateEntryImpl {
    
        public static final int LENGTH_BYTES = 3;
        
        private Vector<Byte> ASN1_subjectPublicKeyInfo;
        
        public RawPublicKey(Vector<Byte> ASN1_subjectPublicKeyInfo, 
                Vector<ExtensionImpl> extensions) {
            
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
                        buf -> ExtensionImpl.parse(buf)));
        }

    }

}
