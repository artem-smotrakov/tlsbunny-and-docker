package com.gypsyengineer.tlsbunny.tls13;

import com.gypsyengineer.tlsbunny.tls.Entity;
import com.gypsyengineer.tlsbunny.tls.Vector;
import java.io.IOException;
import java.nio.ByteBuffer;

public class KeyShare {
    
    public static class ClientHello implements Entity {
        
        public static final int LENGTH_BYTES = 2;
        public static final int MAX_EXPECTED_LENGTH = 65535;

        private Vector<KeyShareEntry> client_shares;

        public ClientHello() {
            this(Vector.wrap(LENGTH_BYTES));
        }

        public ClientHello(Vector<KeyShareEntry> client_shares) {
            this.client_shares = client_shares;
        }

        public void set(KeyShareEntry keyShareEntry) {
            client_shares = Vector.wrap(MAX_EXPECTED_LENGTH, keyShareEntry);
        }

        public void set(Vector<KeyShareEntry> client_shares) {
            this.client_shares = client_shares;
        }

        public void clear() {
            client_shares.clear();
        }
        
        @Override
        public int encodingLength() {
            return client_shares.encodingLength();
        }

        @Override
        public byte[] encoding() throws IOException {
            return client_shares.encoding();
        }

        public static ClientHello create(KeyShareEntry... keyShareEntries) {
            return new ClientHello(
                    Vector.wrap(LENGTH_BYTES, keyShareEntries));
        }

        public static ClientHello parse(ByteBuffer buffer) {
            Vector<KeyShareEntry> client_shares = Vector.parse(
                    buffer, LENGTH_BYTES, (buf) -> KeyShareEntry.parse(buf));

            return new ClientHello(client_shares);
        }
        
        public static ClientHello parse(Vector<Byte> extenstion_data) 
                throws IOException {
            
            return parse(ByteBuffer.wrap(extenstion_data.bytes()));
        }
    
    }
    
    public static class ServerHello implements Entity {

        private KeyShareEntry server_share;

        public ServerHello(KeyShareEntry server_share) {
            this.server_share = server_share;
        }

        public KeyShareEntry getServerShare() {
            return server_share;
        }

        public void setServerShare(KeyShareEntry server_share) {
            this.server_share = server_share;
        }
        
        @Override
        public int encodingLength() {
            return server_share.encodingLength();
        }

        @Override
        public byte[] encoding() throws IOException {
            return server_share.encoding();
        }

        public static ServerHello parse(ByteBuffer buffer) {
            return new ServerHello(KeyShareEntry.parse(buffer));
        }
        
        public static ServerHello parse(Vector<Byte> extenstion_data) 
                throws IOException {
            
            return parse(ByteBuffer.wrap(extenstion_data.bytes()));
        }

    }
    
    public static class HelloRetryRequest implements Entity {

        private NamedGroup selected_group;

        private HelloRetryRequest(NamedGroup selected_group) {
            this.selected_group = selected_group;
        }

        public NamedGroup getSelectedGroup() {
            return selected_group;
        }

        public void setSelectedGroup(NamedGroup selected_group) {
            this.selected_group = selected_group;
        }
        
        @Override
        public int encodingLength() {
            return selected_group.encodingLength();
        }

        @Override
        public byte[] encoding() throws IOException {
            return selected_group.encoding();
        }

        public static HelloRetryRequest parse(ByteBuffer buffer) {
            return new HelloRetryRequest(NamedGroup.parse(buffer));
        }
        
        public static HelloRetryRequest parse(Vector<Byte> extenstion_data) 
                throws IOException {
            
            return parse(ByteBuffer.wrap(extenstion_data.bytes()));
        }

    }
}
