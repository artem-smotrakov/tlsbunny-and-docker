package com.gypsyengineer.tlsbunny.tls13.connection.action.simple;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;
import com.gypsyengineer.tlsbunny.tls13.crypto.AEAD;
import com.gypsyengineer.tlsbunny.tls13.crypto.AEADException;
import com.gypsyengineer.tlsbunny.tls13.struct.Handshake;

import java.io.IOException;

import static com.gypsyengineer.tlsbunny.tls13.handshake.Context.ZERO_HASH_VALUE;
import static com.gypsyengineer.tlsbunny.tls13.handshake.Context.ZERO_SALT;
import static com.gypsyengineer.tlsbunny.utils.Utils.concatenate;
import static com.gypsyengineer.tlsbunny.utils.Utils.zeroes;

public class ComputingKeysAfterServerHello extends AbstractAction<ComputingKeysAfterServerHello> {

    @Override
    public String name() {
        return "computing keys after ServerHello";
    }

    @Override
    public Action run() throws IOException, AEADException {
        byte[] psk = zeroes(context.hkdf.getHashLength());
        Handshake clientHello = context.getFirstClientHello();
        Handshake serverHello = context.getServerHello();

        context.early_secret = context.hkdf.extract(ZERO_SALT, psk);
        context.binder_key = context.hkdf.deriveSecret(
                context.early_secret,
                concatenate(context.ext_binder, context.res_binder));
        context.client_early_traffic_secret = context.hkdf.deriveSecret(
                context.early_secret,
                context.c_e_traffic,
                clientHello);
        context.early_exporter_master_secret = context.hkdf.deriveSecret(
                context.early_secret,
                context.e_exp_master,
                clientHello);

        context.handshake_secret_salt = context.hkdf.deriveSecret(
                context.early_secret, context.derived);

        context.handshake_secret = context.hkdf.extract(
                context.handshake_secret_salt, context.dh_shared_secret);
        context.client_handshake_traffic_secret = context.hkdf.deriveSecret(
                context.handshake_secret,
                context.c_hs_traffic,
                clientHello, serverHello);
        context.server_handshake_traffic_secret = context.hkdf.deriveSecret(
                context.handshake_secret,
                context.s_hs_traffic,
                clientHello, serverHello);
        context.master_secret = context.hkdf.extract(
                context.hkdf.deriveSecret(context.handshake_secret, context.derived),
                zeroes(context.hkdf.getHashLength()));

        context.client_handshake_write_key = context.hkdf.expandLabel(
                context.client_handshake_traffic_secret,
                context.key,
                ZERO_HASH_VALUE,
                context.suite.keyLength());
        context.client_handshake_write_iv = context.hkdf.expandLabel(
                context.client_handshake_traffic_secret,
                context.iv,
                ZERO_HASH_VALUE,
                context.suite.ivLength());
        context.server_handshake_write_key = context.hkdf.expandLabel(
                context.server_handshake_traffic_secret,
                context.key,
                ZERO_HASH_VALUE,
                context.suite.keyLength());
        context.server_handshake_write_iv = context.hkdf.expandLabel(
                context.server_handshake_traffic_secret,
                context.iv,
                ZERO_HASH_VALUE,
                context.suite.ivLength());
        context.finished_key = context.hkdf.expandLabel(
                context.client_handshake_traffic_secret,
                context.finished,
                ZERO_HASH_VALUE,
                context.hkdf.getHashLength());

        context.handshakeEncryptor = AEAD.createEncryptor(
                context.suite.cipher(),
                context.client_handshake_write_key,
                context.client_handshake_write_iv);
        context.handshakeDecryptor = AEAD.createDecryptor(
                context.suite.cipher(),
                context.server_handshake_write_key,
                context.server_handshake_write_iv);

        return this;
    }

}
