package com.gypsyengineer.tlsbunny.tls13;

import com.gypsyengineer.tlsbunny.tls.Entity;
import com.gypsyengineer.tlsbunny.tls.Vector;
import java.io.IOException;

public class SupportedVersions {

    public static class ClientHello implements Entity {
    
        public static final int VERSIONS_LENGTH_BYTES = 1;
        public static final int MIN_EXPECTED_LENGTH = 2;
        public static final int MAX_EXPECTED_LENGTH = 254;
        
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
    }
    
    public class ServerHello implements Entity {
        
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
