package com.gypsyengineer.tlsbunny.tls13.connection;

import java.nio.ByteBuffer;

public class OutgoingApplicationData extends AbstractAction {

    @Override
    boolean runImpl(ByteBuffer buffer) throws Exception {
        return false;
    }
}
