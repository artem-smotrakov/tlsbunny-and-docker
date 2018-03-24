package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.crypto.AEAD;
import com.gypsyengineer.tlsbunny.tls13.struct.*;

import static com.gypsyengineer.tlsbunny.tls13.handshake.Context.ZERO_HASH_VALUE;
import static com.gypsyengineer.tlsbunny.tls13.handshake.Context.ZERO_SALT;
import static com.gypsyengineer.tlsbunny.utils.Utils.concatenate;
import static com.gypsyengineer.tlsbunny.utils.Utils.info;

import java.io.IOException;

public class IncomingServerHello extends AbstractReceivingAction {

    @Override
    public String name() {
        return "receiving ServerHello";
    }

    @Override
    void runImpl() throws Exception {
        TLSPlaintext tlsPlaintext = factory.parser().parseTLSPlaintext(buffer);
        if (!tlsPlaintext.containsHandshake()) {
            throw new IOException("expected a handshake message");
        }

        Handshake handshake = factory.parser().parseHandshake(tlsPlaintext.getFragment());
        if (!handshake.containsServerHello()) {
            throw new IOException("expected a ServerHello message");
        }

        processServerHello(handshake);
    }

    private void processServerHello(Handshake handshake) throws Exception {
        ServerHello serverHello = factory.parser().parseServerHello(handshake.getBody());

        if (!suite.equals(serverHello.getCipherSuite())) {
            info("expected cipher suite: %s", suite);
            info("received cipher suite: %s", serverHello.getCipherSuite());
            throw new RuntimeException("unexpected ciphersuite");
        }

        SupportedVersions.ServerHello selected_version = findSupportedVersion(serverHello);
        if (!selected_version.equals(ProtocolVersion.TLSv13)) {
            info("ServerHello.selected version: %s", selected_version.getSelectedVersion());
            // TODO: when TLS 1.3 spec is finished, we should throw an exception here
        }

        KeyShare.ServerHello keyShare = findKeyShare(serverHello);
        if (!group.equals(keyShare.getServerShare().getNamedGroup())) {
            info("expected group: %s", group);
            info("received group: %s", keyShare.getServerShare().getNamedGroup());
            throw new RuntimeException("unexpected group");
        }

        negotiator.processKeyShareEntry(keyShare.getServerShare());
        context.dh_shared_secret = negotiator.generateSecret();

        context.setServerHello(handshake);

        byte[] psk = zeroes(hkdf.getHashLength());

        Handshake wrappedClientHello = context.getFirstClientHello();

        context.early_secret = hkdf.extract(ZERO_SALT, psk);
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
                ZERO_HASH_VALUE,
                suite.keyLength());
        context.client_handshake_write_iv = hkdf.expandLabel(
                context.client_handshake_traffic_secret,
                context.iv,
                ZERO_HASH_VALUE,
                suite.ivLength());
        context.server_handshake_write_key = hkdf.expandLabel(
                context.server_handshake_traffic_secret,
                context.key,
                ZERO_HASH_VALUE,
                suite.keyLength());
        context.server_handshake_write_iv = hkdf.expandLabel(
                context.server_handshake_traffic_secret,
                context.iv,
                ZERO_HASH_VALUE,
                suite.ivLength());
        context.finished_key = hkdf.expandLabel(
                context.client_handshake_traffic_secret,
                context.finished,
                ZERO_HASH_VALUE,
                hkdf.getHashLength());

        context.handshakeEncryptor = AEAD.createEncryptor(
                suite.cipher(),
                context.client_handshake_write_key,
                context.client_handshake_write_iv);
        context.handshakeDecryptor = AEAD.createDecryptor(
                suite.cipher(),
                context.server_handshake_write_key,
                context.server_handshake_write_iv);
    }

    private KeyShare.ServerHello findKeyShare(ServerHello hello)
            throws IOException {

        return factory.parser()
                .parseKeyShareFromServerHello(
                    hello.findExtension(ExtensionType.key_share)
                            .getExtensionData().bytes());
    }

    private SupportedVersions.ServerHello findSupportedVersion(ServerHello hello)
            throws IOException {

        return factory.parser().parseSupportedVersionsServerHello(
                hello.findExtension(ExtensionType.supported_versions)
                        .getExtensionData().bytes());
    }

    private static byte[] zeroes(int length) {
        return new byte[length];
    }
}
