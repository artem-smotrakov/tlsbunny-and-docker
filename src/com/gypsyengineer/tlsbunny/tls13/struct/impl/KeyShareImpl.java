package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.struct.KeyShare;
import com.gypsyengineer.tlsbunny.tls13.struct.KeyShareEntry;
import com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup;
import java.io.IOException;

public abstract class KeyShareImpl implements KeyShare {
    
    public static class ClientHelloImpl extends KeyShareImpl implements ClientHello {

        private final Vector<KeyShareEntry> client_shares;

        ClientHelloImpl() {
            this(Vector.wrap(LENGTH_BYTES));
        }

        public ClientHelloImpl(Vector<KeyShareEntry> client_shares) {
            this.client_shares = client_shares;
        }

        @Override
        public int encodingLength() {
            return client_shares.encodingLength();
        }

        @Override
        public byte[] encoding() throws IOException {
            return client_shares.encoding();
        }

    }
    
    public static class ServerHelloImpl extends KeyShareImpl implements ServerHello {

        private final KeyShareEntry server_share;

        ServerHelloImpl(KeyShareEntry server_share) {
            this.server_share = server_share;
        }

        @Override
        public KeyShareEntry getServerShare() {
            return server_share;
        }

        @Override
        public int encodingLength() {
            return server_share.encodingLength();
        }

        @Override
        public byte[] encoding() throws IOException {
            return server_share.encoding();
        }

    }
    
    public static class HelloRetryRequestImpl extends KeyShareImpl 
            implements HelloRetryRequest {

        private final NamedGroup selected_group;

        HelloRetryRequestImpl(NamedGroup selected_group) {
            this.selected_group = selected_group;
        }

        @Override
        public NamedGroup getSelectedGroup() {
            return selected_group;
        }

        @Override
        public int encodingLength() {
            return selected_group.encodingLength();
        }

        @Override
        public byte[] encoding() throws IOException {
            return selected_group.encoding();
        }

    }
}
