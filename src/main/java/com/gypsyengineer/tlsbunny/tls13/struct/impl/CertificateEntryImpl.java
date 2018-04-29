package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.CertificateEntry;
import com.gypsyengineer.tlsbunny.tls13.struct.Extension;
import com.gypsyengineer.tlsbunny.tls13.struct.ExtensionType;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;

public abstract class CertificateEntryImpl implements CertificateEntry {

    final Vector<Extension> extensions;

    CertificateEntryImpl(Vector<Extension> extensions) {
        this.extensions = extensions;
    }

    @Override
    public Vector<Extension> getExtensions() {
        return extensions;
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
    
        private final Vector<Byte> cert_data;
        
        public X509Impl(Vector<Byte> cert_data, Vector<Extension> extensions) {
            super(extensions);
            this.cert_data = cert_data;
        }

        @Override
        public Vector<Byte> getCertData() {
            return cert_data;
        }

        @Override
        public int encodingLength() {
            return Utils.getEncodingLength(cert_data, extensions);
        }

        @Override
        public byte[] encoding() throws IOException {
            return Utils.encoding(cert_data, extensions);
        }       

    }

    public static class RawPublicKeyImpl extends CertificateEntryImpl 
            implements RawPublicKey {
    
        private final Vector<Byte> ASN1_subjectPublicKeyInfo;
        
        public RawPublicKeyImpl(Vector<Byte> ASN1_subjectPublicKeyInfo, 
                Vector<Extension> extensions) {
            
            super(extensions);
            this.ASN1_subjectPublicKeyInfo = ASN1_subjectPublicKeyInfo;
        }

        @Override
        public Vector<Byte> getASN1SubjectPublicKeyInfo() {
            return ASN1_subjectPublicKeyInfo;
        }

        @Override
        public int encodingLength() {
            return Utils.getEncodingLength(ASN1_subjectPublicKeyInfo, extensions);
        }

        @Override
        public byte[] encoding() throws IOException {
            return Utils.encoding(ASN1_subjectPublicKeyInfo, extensions);
        }       

    }

}
