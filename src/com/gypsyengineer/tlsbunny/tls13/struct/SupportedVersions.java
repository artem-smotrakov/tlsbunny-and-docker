package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Vector;
import java.io.IOException;
import java.nio.ByteBuffer;
import com.gypsyengineer.tlsbunny.tls.Struct;

public abstract class SupportedVersions implements Struct {

    public static class ClientHello extends SupportedVersions {
    
        public static final int VERSIONS_LENGTH_BYTES = 1;
        
        private Vector<ProtocolVersion> versions;
        
        public ClientHello(Vector<ProtocolVersion> versions) {
            this.versions = versions;
        }
        
        public Vector<ProtocolVersion> getVersions() {
            return versions;
        }
        
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
        
        public static ClientHello create(ProtocolVersion version) {
            return new ClientHello(Vector.wrap(VERSIONS_LENGTH_BYTES, version));
        }
        
        public static ClientHello parse(Vector<Byte> extension_data) throws IOException {
            return parse(ByteBuffer.wrap(extension_data.bytes()));
        }
        
        public static ClientHello parse(ByteBuffer buffer) {
            return new ClientHello(
                    Vector.parse(
                            buffer, 
                            VERSIONS_LENGTH_BYTES, 
                            buf -> ProtocolVersion.parse(buf)));
        }
    }
    
    public static class ServerHello extends SupportedVersions {
        
        private ProtocolVersion selected_version;

        public ServerHello(ProtocolVersion selected_version) {
            this.selected_version = selected_version;
        }

        public ProtocolVersion getSelectedVersion() {
            return selected_version;
        }

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
