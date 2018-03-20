package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.crypto.AEAD;
import com.gypsyengineer.tlsbunny.tls13.struct.ExtensionType;
import com.gypsyengineer.tlsbunny.tls13.struct.Handshake;
import com.gypsyengineer.tlsbunny.tls13.struct.KeyShare;
import com.gypsyengineer.tlsbunny.tls13.struct.ServerHello;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;
import static com.gypsyengineer.tlsbunny.utils.Utils.concatenate;
import java.io.IOException;
import java.nio.ByteBuffer;

public class IncomingServerHello extends AbstractReceivingAction {

    @Override
    boolean runImpl(ByteBuffer buffer) throws Exception {
        TLSPlaintext tlsPlaintext = factory.parser().parseTLSPlaintext(buffer);
        if (!tlsPlaintext.containsHandshake()) {
            throw new IOException("expected a handshake message");
        }

        Handshake handshake = factory.parser().parseHandshake(tlsPlaintext.getFragment());
        if (!handshake.containsServerHello()) {
            throw new IOException("expected a ServerHello message");
        }

        return processServerHello(handshake);
    }

    private boolean processServerHello(Handshake handshake) throws Exception {
        ServerHello serverHello = factory.parser().parseServerHello(handshake.getBody());
        KeyShare.ServerHello keyShare = findKeyShare(serverHello);

        negotiator.processKeyShareEntry(keyShare.getServerShare());
        context.dh_shared_secret = negotiator.generateSecret();

        context.setServerHello(handshake);
        if (!suite.equals(serverHello.getCipherSuite())) {
            throw new RuntimeException();
        }

        byte[] zero_psk = zeroes(hkdf.getHashLength());
        byte[] zero_salt = new byte[0];
        byte[] zero_hash_value = new byte[0];

        Handshake wrappedClientHello = context.getFirstClientHello();

        context.early_secret = hkdf.extract(zero_salt, zero_psk);
        context.binder_key = hkdf.deriveSecret(
                context.early_secret,
                concatenate(context.ext_binder, context.res_binder));
        context.client_early_traffic_secret = hkdf.deriveSecret(
                context.early_secret,
                context.c_e_traffic,
                wrappedClientHello);
        context.early_exporter_master_secret = hkdf.deriveSecret(
                context.early_secret,
                context.e_exp_master,
                wrappedClientHello);

        context.handshake_secret_salt = hkdf.deriveSecret(
                context.early_secret, context.derived);

        context.handshake_secret = hkdf.extract(
                context.handshake_secret_salt, context.dh_shared_secret);
        context.client_handshake_traffic_secret = hkdf.deriveSecret(
                context.handshake_secret,
                context.c_hs_traffic,
                wrappedClientHello, handshake);
        context.server_handshake_traffic_secret = hkdf.deriveSecret(
                context.handshake_secret,
                context.s_hs_traffic,
                wrappedClientHello, handshake);
        context.master_secret = hkdf.extract(
                hkdf.deriveSecret(context.handshake_secret, context.derived),
                zeroes(hkdf.getHashLength()));

        context.client_handshake_write_key = hkdf.expandLabel(
                context.client_handshake_traffic_secret,
                context.key,
                zero_hash_value,
                suite.keyLength());
        context.client_handshake_write_iv = hkdf.expandLabel(
                context.client_handshake_traffic_secret,
                context.iv,
                zero_hash_value,
                suite.ivLength());
        context.server_handshake_write_key = hkdf.expandLabel(
                context.server_handshake_traffic_secret,
                context.key,
                zero_hash_value,
                suite.keyLength());
        context.server_handshake_write_iv = hkdf.expandLabel(
                context.server_handshake_traffic_secret,
                context.iv,
                zero_hash_value,
                suite.ivLength());
        context.finished_key = hkdf.expandLabel(
                context.client_handshake_traffic_secret,
                context.finished,
                zero_hash_value,
                hkdf.getHashLength());

        context.handshakeEncryptor = AEAD.createEncryptor(
                suite.cipher(),
                context.client_handshake_write_key,
                context.client_handshake_write_iv);
        context.handshakeDecryptor = AEAD.createDecryptor(
                suite.cipher(),
                context.server_handshake_write_key,
                context.server_handshake_write_iv);

        return true;
    }

    private KeyShare.ServerHello findKeyShare(ServerHello hello)
            throws IOException {

        return factory.parser()
                .parseKeyShareFromServerHello(
                    hello.findExtension(ExtensionType.key_share)
                            .getExtensionData().bytes());
    }

    private static byte[] zeroes(int length) {
        return new byte[length];
    }
}
