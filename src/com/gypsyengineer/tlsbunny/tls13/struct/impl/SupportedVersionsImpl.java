package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion;
import com.gypsyengineer.tlsbunny.tls13.struct.SupportedVersions;
import java.io.IOException;

public abstract class SupportedVersionsImpl implements SupportedVersions {

    public static class ClientHelloImpl implements ClientHello {
        
        private Vector<ProtocolVersion> versions;
        
        ClientHelloImpl(Vector<ProtocolVersion> versions) {
            this.versions = versions;
        }
        
        @Override
        public Vector<ProtocolVersion> getVersions() {
            return versions;
        }
        
        @Override
        public void setVersions(Vector<ProtocolVersion> versions) {
            this.versions = versions;
        }

        @Override
        public int encodingLength() {
            return versions.encodingLength();
        }

        @Override
        public byte[] encoding() throws IOException {
            return versions.encoding();
        }
        
    }
    
    public static class ServerHelloImpl implements SupportedVersions.ServerHello {
        
        private ProtocolVersion selected_version;

        ServerHelloImpl(ProtocolVersion selected_version) {
            this.selected_version = selected_version;
        }

        @Override
        public ProtocolVersion getSelectedVersion() {
            return selected_version;
        }

        @Override
        public void setSelectedVersion(ProtocolVersion selected_version) {
            this.selected_version = selected_version;
        }

        @Override
        public int encodingLength() {
            return selected_version.encodingLength();
        }

        @Override
        public byte[] encoding() throws IOException {
            return selected_version.encoding();
        }
        
    }
}
