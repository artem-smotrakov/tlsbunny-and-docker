package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Vector;
import java.io.IOException;
import java.nio.ByteBuffer;
import com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion;
import com.gypsyengineer.tlsbunny.tls13.struct.SupportedVersions;

public abstract class SupportedVersionsImpl implements SupportedVersions {

    public static class ClientHelloImpl implements ClientHello {
        
        private Vector<ProtocolVersion> versions;
        
        public ClientHelloImpl(Vector<ProtocolVersion> versions) {
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
        
        public static ClientHelloImpl parse(Vector<Byte> extension_data) throws IOException {
            return parse(ByteBuffer.wrap(extension_data.bytes()));
        }
        
        public static ClientHelloImpl parse(ByteBuffer buffer) {
            return new ClientHelloImpl(
                    Vector.parse(
                            buffer, 
                            VERSIONS_LENGTH_BYTES, 
                            buf -> ProtocolVersionImpl.parse(buf)));
        }
    }
    
    public static class ServerHelloImpl implements SupportedVersions.ServerHello {
        
        private ProtocolVersionImpl selected_version;

        public ServerHelloImpl(ProtocolVersionImpl selected_version) {
            this.selected_version = selected_version;
        }

        @Override
        public ProtocolVersionImpl getSelectedVersion() {
            return selected_version;
        }

        @Override
        public void setSelectedVersion(ProtocolVersionImpl selected_version) {
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
