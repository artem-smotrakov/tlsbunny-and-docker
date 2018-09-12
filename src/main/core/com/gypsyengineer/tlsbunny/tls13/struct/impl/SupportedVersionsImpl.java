package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion;
import com.gypsyengineer.tlsbunny.tls13.struct.SupportedVersions;
import java.io.IOException;

public abstract class SupportedVersionsImpl implements SupportedVersions {

    public static class ClientHelloImpl implements ClientHello {
        
        private final Vector<ProtocolVersion> versions;
        
        ClientHelloImpl(Vector<ProtocolVersion> versions) {
            this.versions = versions;
        }
        
        @Override
        public Vector<ProtocolVersion> getVersions() {
            return versions;
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
        
        private final ProtocolVersion selected_version;

        ServerHelloImpl(ProtocolVersion selected_version) {
            this.selected_version = selected_version;
        }

        @Override
        public ProtocolVersion getSelectedVersion() {
            return selected_version;
        }

        @Override
        public int encodingLength() {
            return selected_version.encodingLength();
        }

        @Override
        public byte[] encoding() throws IOException {
            return selected_version.encoding();
        }

        @Override
        public String toString() {
            return String.format("SupportedVersion { ServerHello, selected_version: %s }", selected_version);
        }
        
    }
}
