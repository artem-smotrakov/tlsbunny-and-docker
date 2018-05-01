package com.gypsyengineer.tlsbunny.tls13.connection.action.simple;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;
import com.gypsyengineer.tlsbunny.tls13.connection.action.ActionFailed;
import com.gypsyengineer.tlsbunny.tls13.crypto.AEADException;

import java.io.IOException;

public class ComputingKeysAfterServerFinished extends AbstractAction {

    @Override
    public String name() {
        return "computing keys after receiving server's Finished";
    }

    @Override
    public Action run() throws IOException {
        context.client_application_traffic_secret_0 = context.hkdf.deriveSecret(
                context.master_secret,
                context.c_ap_traffic,
                context.allMessages());
        context.server_application_traffic_secret_0 = context.hkdf.deriveSecret(
                context.master_secret,
                context.s_ap_traffic,
                context.allMessages());
        context.exporter_master_secret = context.hkdf.deriveSecret(
                context.master_secret,
                context.exp_master,
                context.allMessages());

        return this;
    }

}
