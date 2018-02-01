package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Vector;
import java.io.IOException;
import java.nio.ByteBuffer;
import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls13.struct.KeyShare.ClientHello;
import com.gypsyengineer.tlsbunny.tls13.struct.KeyShare.ServerHello;
import com.gypsyengineer.tlsbunny.tls13.struct.KeyShare.HelloRetryRequest;

public abstract class KeyShareImpl implements Struct {
    
    public static class ClientHelloImpl extends KeyShareImpl implements ClientHello {
        

        private Vector<KeyShareEntryImpl> client_shares;

        public ClientHelloImpl() {
            this(Vector.wrap(LENGTH_BYTES));
        }

        public ClientHelloImpl(Vector<KeyShareEntryImpl> client_shares) {
            this.client_shares = client_shares;
        }

        @Override
        public void set(KeyShareEntryImpl keyShareEntry) {
            client_shares = Vector.wrap(LENGTH_BYTES, keyShareEntry);
        }

        @Override
        public void set(Vector<KeyShareEntryImpl> client_shares) {
            this.client_shares = client_shares;
        }

        @Override
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

        public static ClientHelloImpl create(KeyShareEntryImpl... keyShareEntries) {
            return new ClientHelloImpl(
                    Vector.wrap(LENGTH_BYTES, keyShareEntries));
        }

        public static ClientHelloImpl parse(ByteBuffer buffer) {
            Vector<KeyShareEntryImpl> client_shares = Vector.parse(
                    buffer, LENGTH_BYTES, (buf) -> KeyShareEntryImpl.parse(buf));

            return new ClientHelloImpl(client_shares);
        }
        
        public static ClientHelloImpl parse(Vector<Byte> extenstion_data) 
                throws IOException {
            
            return parse(ByteBuffer.wrap(extenstion_data.bytes()));
        }
    
    }
    
    public static class ServerHelloImpl extends KeyShareImpl implements ServerHello {

        private KeyShareEntryImpl server_share;

        public ServerHelloImpl(KeyShareEntryImpl server_share) {
            this.server_share = server_share;
        }

        @Override
        public KeyShareEntryImpl getServerShare() {
            return server_share;
        }

        @Override
        public void setServerShare(KeyShareEntryImpl server_share) {
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

        public static ServerHelloImpl parse(ByteBuffer buffer) {
            return new ServerHelloImpl(KeyShareEntryImpl.parse(buffer));
        }
        
        public static ServerHelloImpl parse(Vector<Byte> extenstion_data) 
                throws IOException {
            
            return parse(ByteBuffer.wrap(extenstion_data.bytes()));
        }

    }
    
    public static class HelloRetryRequestImpl implements HelloRetryRequest {

        private NamedGroupImpl selected_group;

        private HelloRetryRequestImpl(NamedGroupImpl selected_group) {
            this.selected_group = selected_group;
        }

        @Override
        public NamedGroupImpl getSelectedGroup() {
            return selected_group;
        }

        @Override
        public void setSelectedGroup(NamedGroupImpl selected_group) {
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

        public static HelloRetryRequestImpl parse(ByteBuffer buffer) {
            return new HelloRetryRequestImpl(NamedGroupImpl.parse(buffer));
        }
        
        public static HelloRetryRequestImpl parse(Vector<Byte> extenstion_data) 
                throws IOException {
            
            return parse(ByteBuffer.wrap(extenstion_data.bytes()));
        }

    }
}
