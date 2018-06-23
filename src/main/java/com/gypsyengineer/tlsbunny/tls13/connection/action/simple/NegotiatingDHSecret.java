package com.gypsyengineer.tlsbunny.tls13.connection.action.simple;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;
import com.gypsyengineer.tlsbunny.tls13.handshake.NegotiatorException;
import com.gypsyengineer.tlsbunny.tls13.struct.KeyShare;
import com.gypsyengineer.tlsbunny.tls13.struct.ServerHello;

import java.io.IOException;

import static com.gypsyengineer.tlsbunny.tls13.utils.TLS13Utils.findKeyShare;

public class NegotiatingDHSecret extends AbstractAction<NegotiatingDHSecret> {

    @Override
    public String name() {
        return "negotiating DH secret";
    }

    @Override
    public Action run() throws IOException, NegotiatorException {
        // TODO: we look for only first key share, but there may be multiple key shares
        ServerHello serverHello = context.factory.parser().parseServerHello(
                context.getServerHello().getBody());
        KeyShare.ServerHello keyShare = findKeyShare(context.factory, serverHello);
        if (!context.group.equals(keyShare.getServerShare().getNamedGroup())) {
            output.info("expected group: %s", context.group);
            output.info("received group: %s", keyShare.getServerShare().getNamedGroup());
            throw new RuntimeException("unexpected group");
        }
        context.negotiator.processKeyShareEntry(keyShare.getServerShare());
        context.dh_shared_secret = context.negotiator.generateSecret();

        return this;
    }

}
