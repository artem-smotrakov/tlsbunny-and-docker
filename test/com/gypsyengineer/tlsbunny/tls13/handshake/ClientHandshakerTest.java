package com.gypsyengineer.tlsbunny.tls13.handshake;

import com.gypsyengineer.tlsbunny.tls13.crypto.ApplicationDataChannel;
import com.gypsyengineer.tlsbunny.tls13.crypto.HKDF;
import com.gypsyengineer.tlsbunny.tls13.struct.CipherSuite;
import com.gypsyengineer.tlsbunny.tls13.struct.ClientHello;
import com.gypsyengineer.tlsbunny.tls13.struct.ContentType;
import com.gypsyengineer.tlsbunny.tls13.struct.Handshake;
import com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType;
import com.gypsyengineer.tlsbunny.tls13.struct.KeyShareEntry;
import com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup;
import com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion;
import com.gypsyengineer.tlsbunny.tls13.struct.ServerHello;
import com.gypsyengineer.tlsbunny.tls13.struct.SignatureScheme;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.tls13.struct.TLSPlaintext;
import com.gypsyengineer.tlsbunny.utils.CertificateHolder;
import static com.gypsyengineer.tlsbunny.utils.Convertor.hex2bytes;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Based on https://tools.ietf.org/html/draft-ietf-tls-tls13-vectors-02#section-3
 */
public class ClientHandshakerTest {

    private static final byte[] SECRET = hex2bytes(
            "f677c3cdac26a755 455b130efa9b1a3f"
            + "3cafb153544ca46a ddf670df199d996e");

    private static final byte[] EARLY_SECRET = hex2bytes(
            "33ad0a1c607ec03b 09e6cd9893680ce2"
            + "10adf300aa1f2660 e1b22e10f170f92a");

    private static final byte[] HANDSHAKE_SECRET = hex2bytes(
            "0cefce00d5d29fd0 9f5de36c86fc8e72"
            + "99b4ad11ba4211c6 7063c2cc539fc4f9");

    private static final byte[] HANDSHAKE_SECRET_SALT = hex2bytes(
            "6f2615a108c702c5 678f54fc9dbab697"
            + "16c076189c48250c ebeac3576c3611ba");

    private static final byte[] CURRENT_HASH = hex2bytes(
            "8ac51822361c5963 2de3c6b259e5808c"
            + "e52b8278a6493de2 a976f441abbadc8c");

    private static final byte[] CLIENT_HANDSHAKE_TRAFFIC_SECRET = hex2bytes(
            "5a63db760b817b1b da96e72832333aec"
            + "6a177deeadb5b407 501ac10c17dac0a4");

    private static final byte[] SERVER_HANDSHAKE_TRAFFIC_SECRET = hex2bytes(
            "3aa72a3c77b791e8 f4de243f9ccce172"
            + "941f8392aeb05429 320f4b572ccfe744");

    private static final byte[] MASTER_SECRET = hex2bytes(
            "6c6d4b3e7c925460 82d7b7a32f6ce219"
            + "3804f1bb930fed74 5c6b93c71397f424");

    private static final byte[] CLIENT_HANDSHAKE_WRITE_KEY = hex2bytes("21103162263e8231 34d6916a82b741c2");

    private static final byte[] CLIENT_HANDSHAKE_WRITE_IV = hex2bytes("0e1be2fa84c0bc3c b6d6afe3");

    final byte[] SERVER_HANDSHAKE_WRITE_KEY = hex2bytes("5727465c1d8af9bd dbbaa81aafe54bfb");

    private static final byte[] SERVER_HANDSHAKE_WRITE_IV = hex2bytes("409072c6da71d076 947e7663");

    private static final byte[] CLIENT_APPLICATION_TRAFFIC_SECRET0 = hex2bytes(
            "53b154f7205e2193 3794330173b14118"
            + "bcd02305b39d64b8 e5271737a7402c74");

    private static final byte[] SERVER_APPLICATION_TRAFFIC_SECRET0 = hex2bytes(
            "47603e72ab5a85b4 dc480897acd07e96"
            + "d18e9db0a931bf75 1650698d6512092d");

    private static final byte[] EXPORTER_MASTER_SECRET = hex2bytes(
            "acf49197383cc5fb 50fde04f506dfd58"
            + "68dc798219f5eedf fd4f3b7eb713b0c9");

    private static final byte[] SERVER_APPLICATION_WRITE_KEY = hex2bytes(
            "698b2aa36a58ceac 77776dd2513fa7fa");

    private static final byte[] SERVER_APPLICATION_WRITE_IV = hex2bytes(
            "7fbff5a2c0ac5bd6 7e2cd759");

    private static final byte[] CLIENT_APPLICATION_WRITE_KEY = hex2bytes(
            "459caa9e3914221d 39cc67ae65f9941e");

    private static final byte[] CLEINT_APPLICATION_WRITE_IV = hex2bytes(
            "54123c2ec7106081 0086c391");

    private static final byte[] FINISHED_KEY = hex2bytes(
            "f8acf5aead23c230 5706ce75da058ecb"
            + "f9393fd656dfb95f db225f9990d4732d");

    private static final byte[] ENCODED_CLIENT_HELLO = hex2bytes(
            "010001fc0303af21 156b04db639e6615"
            + "4a1fe5adfaeadf9e 413416000d57b8e1 126d4d119a8b0000"
            + "3e130113031302c0 2bc02fcca9cca8c0 0ac009c013c023c0"
            + "27c014009eccaa00 3300320067003900 38006b0016001300"
            + "9c002f003c003500 3d000a0005000401 0001950000000b00"
            + "0900000673657276 6572ff0100010000 0a00140012001d00"
            + "1700180019010001 0101020103010400 0b00020100002300"
            + "0000280026002400 1d0020da6a859ad6 d2dbb51124fbfe6b"
            + "aff63d8f14365ec9 90d575761e4a6164 978d31002b000706"
            + "7f1503030302000d 0020001e04030503 0603020308040805"
            + "0806040105010601 0201040205020602 0202002d00020101"
            + "001500fc00000000 0000000000000000 0000000000000000"
            + "0000000000000000 0000000000000000 0000000000000000"
            + "0000000000000000 0000000000000000 0000000000000000"
            + "0000000000000000 0000000000000000 0000000000000000"
            + "0000000000000000 0000000000000000 0000000000000000"
            + "0000000000000000 0000000000000000 0000000000000000"
            + "0000000000000000 0000000000000000 0000000000000000"
            + "0000000000000000 0000000000000000 0000000000000000"
            + "0000000000000000 0000000000000000 0000000000000000"
            + "0000000000000000 0000000000000000 0000000000000000"
            + "0000000000000000 0000000000000000");

    private static final byte[] ENCODED_SERVER_HELLO = hex2bytes(
            "0200004e7f15deac 631669eaf28c6b12"
            + "8b2091d36441e618 964dd8f0ec812e31 cda7aec1d0c11301"
            + "002800280024001d 00209d1bfe805304 6d2dbd8e0e6221da"
            + "d11587584713c8cf 497074d9d26d067c 432f");

    private static final byte[] SERVER_CIPHERTEXTS = hex2bytes(
            "170301029cd612d0 b9706b733ac1708a"
            + "fcac1aeec92415c3 7e1c55167e267326 26ef7e4d3e266651"
            + "d1179df924b6c2d2 76eddc07880ff0a8 23925d9d60efffc1"
            + "3b3d5acce6c1e8e1 34aab30052cdabfc f54331057918d2fd"
            + "e22bc67b78b5e2fb e9853fe57aad1319 7f9d22767f6fd6fa"
            + "f82e4c198641fa7e bf6425222d08c310 67a4641ef3e29a7f"
            + "99f704b2ea451b54 e33e1d7749b15ec4 49556d90645a1803"
            + "f3d87dc4b5753556 e5ff1970521f75c5 db3fe7f621c2b47e"
            + "6e5519ab4d7363a1 f7da6f35a9f3587d b3d57ee89a8f24f7"
            + "ba9678a5466497bb 476091cec490a450 b33fdb4978a8fae4"
            + "18f408e3c9e0992a 274eb6718106c4dc 351b8a6b7435ac8b"
            + "2214e194e5edfeff d4a59a2056d6a45c 8f177f39b2b39dcd"
            + "d9813c1fea04e757 6e7a1f5e218bcf8f fbb981e36006dd0b"
            + "b6bb22a1c3d4926c 505f74f231934a57 0c12834d0582e1bf"
            + "2ea9c2280da0b4aa 152f7dd12c81fd48 682076ecd1cd47d4"
            + "149b6352d0975134 3c6b060a61d30ffa 4f8bd1e8a2ab61ff"
            + "3e9f965dfcd7d1c4 7edb2eae8ff132dd fc1f7774ac77b56a"
            + "ce0d43b8d1163638 6538ceb695da7af0 91f18236aab74859"
            + "656e54cf53fd9960 064702b81b664518 65cd8e0d7804708c"
            + "e842204a3dac91ad 826847ce0c3c3f0d e59392fc3b0bbec0"
            + "5878c8f56b68eb50 f62798c86c570f1a d9254fa41b152a77"
            + "6fb17707bfab5ea2 a834e9edd05f6239 204127cc0f5cc18b"
            + "1dae4a070890bdf7 642704b5e9961ff2 6b931d069aeb08dd"
            + "385f1997f804375d 238f26a9e8e8f007 47ea85747d7a7c61"
            + "6493bd0eff96c576 87e1b409469c3c7a 0c40a9b5ca1eeafd"
            + "f1998fbc4a671898 d8b8a37769cc0ecb 6c19f22b87d46968"
            + "b9a4c1b660f39373 ea517cbf401fe5af 0f2cc910e5786af2"
            + "50a392038be62b93 46b166dbb91ebe46 579f020b1e75d771"
            + "be8ab0dcb7ccce81 48");

    private static final byte[] CLIENT_CIPHERTEXTS = hex2bytes(
            "1703010035f879b9 6aca6de41e53173a"
            + "55015f7810bdd941 5ac444002b5d7d19 a221fee902124509"
            + "5a56aa57d42966b0 17e0fcbaa53027d5 ba2e");

    private static final byte[] APPLICATION_DATA_PAYLOAD = hex2bytes(
            "0001020304050607 08090a0b0c0d0e0f"
            + "1011121314151617 18191a1b1c1d1e1f 2021222324252627"
            + "28292a2b2c2d2e2f 3031");

    private static final byte[] CLIENT_APPLICATION_DATA_CIPHERTEXT = hex2bytes(
            "17030100432e3b59 0791333db65b5632"
            + "d4c9c7e066120216 08680e714177b07f 06500f28f27617d8"
            + "a92a52ec167530f4 ee7262e40127b997 5c26499c23d8bf6e"
            + "713c4b0c126733bf");

    private static final byte[] NEW_SESSION_TICKET_CIPHERTEXT = hex2bytes(
            "17030100cea307cb 4a28329dbf6879ee"
            + "56d1cb4e0055f889 169b3a04ee050225 69c1ad70115dc655"
            + "7802c91832e6e5ef b69c65050f06d189 1692561d4ece8d10"
            + "813bf7a3ea3fb430 cbb36ba1a1d71276 d405a8dd0fef782b"
            + "402a8875245eda0b bd548b61639ba45b 9c63689104432850"
            + "f4c7a8a76a2d13a9 746a424a65730fd1 7ab97f3488d93ab4"
            + "ebdc0f9f8b317855 1faf72ca05f705dd 901815887a0f7f6f"
            + "7062a3802259d9f2 7bb30b6875be1743 54d6fa59adf24a6b"
            + "85c5415d46173c85 5aaf0dc06296099f c6daa0164ef2848c 2219ae");

    private static final byte[] SERVER_APPLICATION_DATA_CIPHERTEXT = hex2bytes(
            "1703010043b7a6f5 f971aee65e5386b4"
            + "18f1533c8de304b6 bb58fed0062ca441 d49ea52e219f9c0f"
            + "10fade977cf7ce2a 0e6c9a46ca1b2b72 3b843dc8c630db6e"
            + "64cdb1c27979b6f4");

    private static class TestSecretNegotiator implements Negotiator {

        @Override
        public KeyShareEntry createKeyShareEntry() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void processKeyShareEntry(KeyShareEntry entry) {

        }

        @Override
        public byte[] generateSecret() {
            return SECRET;
        }

    }

    private static class TestClientHandshaker extends ClientHandshaker {

        public TestClientHandshaker() throws NoSuchAlgorithmException, IOException {
            super(StructFactory.getDefault(),
                    SignatureScheme.rsa_pkcs1_sha1,
                    NamedGroup.x25519,
                    new TestSecretNegotiator(),
                    CipherSuite.TLS_AES_128_GCM_SHA256,
                    HKDF.create(
                            CipherSuite.TLS_AES_128_GCM_SHA256.hash(),
                            StructFactory.getDefault()),
                    CertificateHolder.NO_CERTIFICATE);
        }

        @Override
        public ClientHello createClientHello() throws Exception {
            Handshake handshake = factory.parser().parseHandshake(ENCODED_CLIENT_HELLO);
            ClientHello clientHello = factory.parser()
                    .parseClientHello(handshake.getBody());

            Handshake anotherHandshake = toHandshake(clientHello);
            ClientHello anotherClientHello = factory.parser()
                    .parseClientHello(anotherHandshake.getBody());

            assertArrayEquals(clientHello.encoding(), anotherClientHello.encoding());
            assertArrayEquals(ENCODED_CLIENT_HELLO, handshake.encoding());

            getContext().setFirstClientHello(anotherHandshake);

            return clientHello;
        }

    }

    @Test
    public void handshakeTest() throws Exception {
        ClientHandshaker handshaker = new TestClientHandshaker();
        StructFactory factory = StructFactory.getDefault();

        handshaker.createClientHello();
        ServerHello serverHello = createServerHello();
        handshaker.handleServerHello(factory.createHandshake(
                serverHello.type(), serverHello.encoding()));

        assertArrayEquals(EARLY_SECRET, handshaker.getEarlySecret());
        assertArrayEquals(HANDSHAKE_SECRET, handshaker.getHandshakeSecret());
        assertArrayEquals(HANDSHAKE_SECRET_SALT, handshaker.getHandshakeSecretSalt());
        assertArrayEquals(
                ENCODED_CLIENT_HELLO,
                handshaker.getContext().getFirstClientHello().encoding());
        assertArrayEquals(
                ENCODED_SERVER_HELLO,
                handshaker.getContext().getServerHello().encoding());
        assertArrayEquals(CURRENT_HASH, handshaker.getCurrentHash());
        assertArrayEquals(
                CLIENT_HANDSHAKE_TRAFFIC_SECRET,
                handshaker.getClientHandshakeTrafficSecret());
        assertArrayEquals(
                SERVER_HANDSHAKE_TRAFFIC_SECRET,
                handshaker.getServerHandshakeTrafficSecret());
        assertArrayEquals(MASTER_SECRET, handshaker.getMasterSecret());
        assertArrayEquals(
                CLIENT_HANDSHAKE_WRITE_KEY,
                handshaker.getClientHandshakeWriteKey());
        assertArrayEquals(
                CLIENT_HANDSHAKE_WRITE_IV,
                handshaker.getClientHandshakeWriteIv());
        assertArrayEquals(
                SERVER_HANDSHAKE_WRITE_KEY,
                handshaker.getServerHandshakeWriteKey());
        assertArrayEquals(
                SERVER_HANDSHAKE_WRITE_IV,
                handshaker.getServerHandshakeWriteIv());

        TLSPlaintext tlsPlaintext = factory.parser()
                .parseTLSPlaintext(SERVER_CIPHERTEXTS);

        byte[] plaintext = handshaker.decrypt(tlsPlaintext).getContent();
        ByteBuffer buffer = ByteBuffer.wrap(plaintext);
        while (buffer.remaining() > 0) {
            handshaker.handle(factory.parser().parseHandshake(buffer));
        }

        assertArrayEquals(
                CLIENT_APPLICATION_TRAFFIC_SECRET0,
                handshaker.getClientApplicationTrafficSecret0());
        assertArrayEquals(
                SERVER_APPLICATION_TRAFFIC_SECRET0,
                handshaker.getServerApplicationTrafficSecret0());
        assertArrayEquals(
                EXPORTER_MASTER_SECRET,
                handshaker.getExporterMasterSecret());

        Handshake handshake = handshaker.toHandshake(handshaker.createFinished());
        handshaker.getContext().setClientFinished(handshake);
        handshaker.computeKeysAfterClientFinished();
        TLSPlaintext[] tlsPlaintexts = handshaker.encrypt(handshake);
        assertArrayEquals(CLIENT_CIPHERTEXTS, tlsPlaintexts[0].encoding());
        assertArrayEquals(
                SERVER_APPLICATION_WRITE_KEY,
                handshaker.getServerApplicationWriteKey());
        assertArrayEquals(
                SERVER_APPLICATION_WRITE_IV,
                handshaker.getServerApplicationWriteIv());
        assertArrayEquals(
                CLIENT_APPLICATION_WRITE_KEY,
                handshaker.getClientApplicationWriteKey());
        assertArrayEquals(
                CLEINT_APPLICATION_WRITE_IV,
                handshaker.getClientApplicationWriteIv());
        assertArrayEquals(FINISHED_KEY, handshaker.getFinishedKey());

        ApplicationDataChannel applicationData = handshaker.createApplicationDataChannel(null);
        assertArrayEquals(
                CLIENT_APPLICATION_DATA_CIPHERTEXT,
                handshaker.factory.createTLSPlaintext(
                        ContentType.application_data,
                        ProtocolVersion.TLSv12,
                        applicationData.encrypt(APPLICATION_DATA_PAYLOAD)).encoding());

        // read NewSessionTicket
        applicationData.decrypt(
                factory.parser()
                        .parseTLSPlaintext(NEW_SESSION_TICKET_CIPHERTEXT).getFragment());

        assertArrayEquals(
                APPLICATION_DATA_PAYLOAD,
                applicationData.decrypt(
                        factory.parser().parseTLSPlaintext(
                                SERVER_APPLICATION_DATA_CIPHERTEXT).getFragment()));

        System.out.println("Holy cow! The test passed!");
    }

    private static ServerHello createServerHello() throws IOException {
        StructFactory factory = StructFactory.getDefault();
        Handshake handshake = factory.parser().parseHandshake(ENCODED_SERVER_HELLO);

        assertTrue(handshake.containsServerHello());
        assertArrayEquals(ENCODED_SERVER_HELLO, handshake.encoding());

        ServerHello serverHello = factory.parser().parseServerHello(handshake.getBody());
        handshake = factory.createHandshake(
                HandshakeType.server_hello,
                serverHello.encoding());
        assertArrayEquals(ENCODED_SERVER_HELLO, handshake.encoding());

        return factory.parser().parseServerHello(handshake.getBody());
    }

}
