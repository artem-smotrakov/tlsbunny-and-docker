package com.gypsyengineer.tlsbunny.tls13.connection.action.simple;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;
import com.gypsyengineer.tlsbunny.tls13.crypto.AEAD;
import com.gypsyengineer.tlsbunny.tls13.crypto.AEADException;
import com.gypsyengineer.tlsbunny.tls13.handshake.Context;

import java.io.IOException;

import static com.gypsyengineer.tlsbunny.tls13.handshake.Context.ZERO_HASH_VALUE;

public class ComputingKeysAfterServerFinished extends AbstractAction<ComputingKeysAfterServerFinished> {

    @Override
    public String name() {
        return "computing keys after receiving server's Finished";
    }

    @Override
    public Action run() throws IOException, AEADException {
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
        context.resumption_master_secret = context.hkdf.deriveSecret(
                context.master_secret,
                Context.res_master,
                context.allMessages());
        context.client_application_write_key = context.hkdf.expandLabel(
                context.client_application_traffic_secret_0,
                Context.key,
                ZERO_HASH_VALUE,
                context.suite.keyLength());
        context.client_application_write_iv = context.hkdf.expandLabel(
                context.client_application_traffic_secret_0,
                Context.iv,
                ZERO_HASH_VALUE,
                context.suite.ivLength());
        context.server_application_write_key = context.hkdf.expandLabel(
                context.server_application_traffic_secret_0,
                Context.key,
                ZERO_HASH_VALUE,
                context.suite.keyLength());
        context.server_application_write_iv = context.hkdf.expandLabel(
                context.server_application_traffic_secret_0,
                Context.iv,
                ZERO_HASH_VALUE,
                context.suite.ivLength());

        context.applicationDataEnctyptor = AEAD.createEncryptor(
                context.suite.cipher(),
                context.client_application_write_key,
                context.client_application_write_iv);
        context.applicationDataDecryptor = AEAD.createDecryptor(
                context.suite.cipher(),
                context.server_application_write_key,
                context.server_application_write_iv);

        return this;
    }

}
