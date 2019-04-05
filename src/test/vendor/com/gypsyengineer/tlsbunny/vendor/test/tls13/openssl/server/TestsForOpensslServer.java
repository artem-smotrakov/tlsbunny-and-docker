package com.gypsyengineer.tlsbunny.vendor.test.tls13.openssl.server;

import com.gypsyengineer.tlsbunny.vendor.test.tls13.common.server.TestsForServer;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.Utils;
import org.junit.*;

import static com.gypsyengineer.tlsbunny.utils.Achtung.achtung;
import static com.gypsyengineer.tlsbunny.vendor.test.tls13.openssl.OpensslServer.opensslServer;

public class TestsForOpensslServer extends TestsForServer {

    @BeforeClass
    public static void setUp() throws Exception {
        server = opensslServer();
        server.start();
        Utils.waitStart(server);
    }

    @Test
    @Override
    @Ignore
    /**
     * The test is ignored because it fails since the server doesn't send an alert.
     *
     * The TLS 1.3 spec says the following:
     *
     *      Implementations MUST NOT send None-length fragments of Handshake
     *      types, even if those fragments contain padding.
     *
     *  https://tools.ietf.org/html/draft-ietf-tls-tls13-28#section-5.1
     *
     *  Should it expect an alert them
     *  after sending an empty TLSPlaintext message of handshake type?
     */
    public void startWithTLSPlaintextWithHandshake() {
        throw achtung("don't override this method!");
    }

    @Test
    @Override
    @Ignore
    /**
     * The test is ignored because it fails since the server doesn't send an alert.
     *
     * The TLS 1.3 spec says the following:
     *
     *    A change_cipher_spec record received before the first ClientHello message
     *    or after the peer's Finished message MUST be treated as an unexpected record type
     *
     *  https://tools.ietf.org/html/draft-ietf-tls-tls13-28#section-5
     *
     *  Is it a minor bug in OpenSSL?
     */
    public void startWithTLSPlaintextWithCCS() throws Exception {
        throw achtung("don't override this method!");
    }

}
