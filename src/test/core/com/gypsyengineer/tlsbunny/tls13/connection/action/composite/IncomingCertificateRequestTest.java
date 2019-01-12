package com.gypsyengineer.tlsbunny.tls13.connection.action.composite;

import com.gypsyengineer.tlsbunny.tls13.connection.action.ActionFailed;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Phase;
import com.gypsyengineer.tlsbunny.tls13.connection.action.simple.GeneratingCertificateRequest;
import com.gypsyengineer.tlsbunny.tls13.connection.action.simple.WrappingIntoHandshake;
import com.gypsyengineer.tlsbunny.tls13.connection.action.simple.WrappingIntoTLSCiphertext;
import com.gypsyengineer.tlsbunny.tls13.crypto.AEAD;
import com.gypsyengineer.tlsbunny.tls13.crypto.AEADException;
import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.tls13.struct.*;
import com.gypsyengineer.tlsbunny.utils.Output;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;

import static com.gypsyengineer.tlsbunny.tls13.crypto.AEAD.Method.AES_128_GCM;
import static com.gypsyengineer.tlsbunny.tls13.struct.ContentType.handshake;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

public class IncomingCertificateRequestTest {

    @Test
    public void basic() throws IOException, ActionFailed, AEADException {
        try (Output output = new Output()) {
            Context context = context();

            byte[] expected_request_context = new byte[10];

            GeneratingCertificateRequest gcr = new GeneratingCertificateRequest();
            gcr.set(context);
            gcr.set(output);
            gcr.context(expected_request_context);
            gcr.signatures(SignatureScheme.ecdsa_secp521r1_sha512);

            ByteBuffer buffer = gcr.run().out();

            WrappingIntoHandshake wih = new WrappingIntoHandshake();
            wih.type(HandshakeType.certificate_request);
            wih.set(context);
            wih.set(output);
            wih.in(buffer);

            buffer = wih.run().out();

            WrappingIntoTLSCiphertext witc = new WrappingIntoTLSCiphertext(Phase.handshake);
            witc.type(handshake);
            witc.set(context);
            witc.set(output);
            witc.in(buffer);

            buffer = witc.run().out();

            IncomingCertificateRequest icr = new IncomingCertificateRequest();
            icr.set(context);
            icr.set(output);
            icr.in(buffer);

            icr.run();

            byte[] actual_request_context = context.certificateRequestContext().bytes();
            assertArrayEquals(expected_request_context, actual_request_context);

            output.info(icr.name());
        }
    }

    @Test
    public void notCertificateRequest() throws IOException, AEADException {
        try (Output output = new Output()) {
            Context context = context();

            byte[] expected_request_context = new byte[10];

            GeneratingCertificateRequest gcr = new GeneratingCertificateRequest();
            gcr.set(context);
            gcr.set(output);
            gcr.context(expected_request_context);
            gcr.signatures(SignatureScheme.ecdsa_secp521r1_sha512);

            ByteBuffer buffer = gcr.run().out();

            WrappingIntoHandshake wih = new WrappingIntoHandshake();
            wih.type(HandshakeType.client_hello);
            wih.set(context);
            wih.set(output);
            wih.in(buffer);

            buffer = wih.run().out();

            WrappingIntoTLSCiphertext witc = new WrappingIntoTLSCiphertext(Phase.handshake);
            witc.type(handshake);
            witc.set(context);
            witc.set(output);
            witc.in(buffer);

            buffer = witc.run().out();

            IncomingCertificateRequest icr = new IncomingCertificateRequest();
            icr.set(context);
            icr.set(output);
            icr.in(buffer);

            icr.run();

            fail("no exception was thrown!");
        } catch (ActionFailed e) {
            // good
        }
    }

    @Test
    public void noSignatures() throws ActionFailed, AEADException {
        try (Output output = new Output()) {
            Context context = context();

            byte[] expected_request_context = new byte[10];

            GeneratingCertificateRequest gcr = new GeneratingCertificateRequest();
            gcr.set(context);
            gcr.set(output);
            gcr.context(expected_request_context);

            ByteBuffer buffer = gcr.run().out();

            WrappingIntoHandshake wih = new WrappingIntoHandshake();
            wih.type(HandshakeType.certificate_request);
            wih.set(context);
            wih.set(output);
            wih.in(buffer);

            buffer = wih.run().out();

            WrappingIntoTLSCiphertext witc = new WrappingIntoTLSCiphertext(Phase.handshake);
            witc.type(handshake);
            witc.set(context);
            witc.set(output);
            witc.in(buffer);

            buffer = witc.run().out();

            IncomingCertificateRequest icr = new IncomingCertificateRequest();
            icr.set(context);
            icr.set(output);
            icr.in(buffer);

            icr.run();

            fail("no exception was thrown!");
        } catch (IOException e) {
            // good
        }
    }

    private static Context context() throws AEADException {
        Context context = new Context();
        context.set(StructFactory.getDefault());

        byte[] key = new byte[16];
        byte[] iv = new byte[16];
        AEAD encryptor = AEAD.createEncryptor(AES_128_GCM, key, iv);
        AEAD decryptor = AEAD.createDecryptor(AES_128_GCM, key, iv);
        context.handshakeEncryptor(encryptor);
        context.handshakeDecryptor(decryptor);

        return context;
    }
}
