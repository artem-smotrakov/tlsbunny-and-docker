package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.ExtensionTypeImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.ExtensionImpl;
import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;
import java.nio.ByteBuffer;
import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls13.struct.*; // TODO
import com.gypsyengineer.tlsbunny.tls13.struct.Extension;

public abstract class CertificateEntryImpl implements CertificateEntry {

    Vector<Extension> extensions;

    CertificateEntryImpl(Vector<Extension> extensions) {
        this.extensions = extensions;
    }

    @Override
    public Vector<Extension> getExtensions() {
        return extensions;
    }

    @Override
    public void setExtensions(Vector<Extension> extensions) {
        this.extensions = extensions;
    }

    @Override
    public void addExtension(Extension extension) {
        extensions.add(extension);
    }

    @Override
    public void clearExtensions() {
        extensions.clear();
    }

    @Override
    public Extension getExtension(ExtensionType type) {
        for (Extension extension : extensions.toList()) {
            if (type.equals(extension.getExtensionType())) {
                return extension;
            }
        }

        return null;
    }
    
    public static class X509Impl extends CertificateEntryImpl implements X509 {
    
        private Vector<Byte> cert_data;
        
        public X509Impl(Vector<Byte> cert_data, Vector<Extension> extensions) {
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
            return new X509Impl(
                    Vector.parseOpaqueVector(buffer, LENGTH_BYTES), 
                    Vector.parse(
                        buffer,
                        EXTENSIONS_LENGTH_BYTES,
                        buf -> ExtensionImpl.parse(buf)));
        }
        
        public static X509 wrap(byte[] bytes) {
            return new X509Impl(
                    Vector.wrap(LENGTH_BYTES, bytes), 
                    Vector.wrap(EXTENSIONS_LENGTH_BYTES));
        }

    }

    public static class RawPublicKeyImpl extends CertificateEntryImpl implements RawPublicKey {
    
        private Vector<Byte> ASN1_subjectPublicKeyInfo;
        
        public RawPublicKeyImpl(Vector<Byte> ASN1_subjectPublicKeyInfo, 
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

        public static RawPublicKeyImpl parse(ByteBuffer buffer) {
            return new RawPublicKeyImpl(
                    Vector.parseOpaqueVector(buffer, LENGTH_BYTES), 
                    Vector.parse(
                        buffer,
                        EXTENSIONS_LENGTH_BYTES,
                        buf -> ExtensionImpl.parse(buf)));
        }

    }

}
