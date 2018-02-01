package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Vector;
import java.io.IOException;
import java.nio.ByteBuffer;
import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls13.struct.KeyShare.ClientHello;
import com.gypsyengineer.tlsbunny.tls13.struct.KeyShare.ServerHello;
import com.gypsyengineer.tlsbunny.tls13.struct.KeyShare.HelloRetryRequest;
import com.gypsyengineer.tlsbunny.tls13.struct.*;

public abstract class KeyShareImpl implements Struct {
    
    public static class ClientHelloImpl extends KeyShareImpl implements ClientHello {

        private Vector<KeyShareEntry> client_shares;

        public ClientHelloImpl() {
            this(Vector.wrap(LENGTH_BYTES));
        }

        public ClientHelloImpl(Vector<KeyShareEntry> client_shares) {
            this.client_shares = client_shares;
        }

        @Override
        public void set(KeyShareEntry keyShareEntry) {
            client_shares = Vector.wrap(LENGTH_BYTES, keyShareEntry);
        }

        @Override
        public void set(Vector<KeyShareEntry> client_shares) {
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

        public static ClientHello create(KeyShareEntry... keyShareEntries) {
            return new ClientHelloImpl(
                    Vector.wrap(LENGTH_BYTES, keyShareEntries));
        }

        public static ClientHello parse(ByteBuffer buffer) {
            Vector<KeyShareEntry> client_shares = Vector.parse(
                    buffer, LENGTH_BYTES, (buf) -> KeyShareEntryImpl.parse(buf));

            return new ClientHelloImpl(client_shares);
        }
        
        public static ClientHello parse(Vector<Byte> extenstion_data) 
                throws IOException {
            
            return parse(ByteBuffer.wrap(extenstion_data.bytes()));
        }
    
    }
    
    public static class ServerHelloImpl extends KeyShareImpl implements ServerHello {

        private KeyShareEntry server_share;

        public ServerHelloImpl(KeyShareEntry server_share) {
            this.server_share = server_share;
        }

        @Override
        public KeyShareEntry getServerShare() {
            return server_share;
        }

        @Override
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

        public static ServerHelloImpl parse(ByteBuffer buffer) {
            return new ServerHelloImpl(KeyShareEntryImpl.parse(buffer));
        }
        
        public static ServerHelloImpl parse(Vector<Byte> extenstion_data) 
                throws IOException {
            
            return parse(ByteBuffer.wrap(extenstion_data.bytes()));
        }

    }
    
    public static class HelloRetryRequestImpl extends KeyShareImpl implements HelloRetryRequest {

        private NamedGroup selected_group;

        private HelloRetryRequestImpl(NamedGroup selected_group) {
            this.selected_group = selected_group;
        }

        @Override
        public NamedGroup getSelectedGroup() {
            return selected_group;
        }

        @Override
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

        public static HelloRetryRequestImpl parse(ByteBuffer buffer) {
            return new HelloRetryRequestImpl(NamedGroupImpl.parse(buffer));
        }
        
        public static HelloRetryRequestImpl parse(Vector<Byte> extenstion_data) 
                throws IOException {
            
            return parse(ByteBuffer.wrap(extenstion_data.bytes()));
        }

    }
}
