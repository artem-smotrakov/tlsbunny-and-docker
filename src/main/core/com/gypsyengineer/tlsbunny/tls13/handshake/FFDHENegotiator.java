package com.gypsyengineer.tlsbunny.tls13.handshake;

import com.gypsyengineer.tlsbunny.tls13.struct.KeyShareEntry;
import com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.utils.Convertor;

import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHPublicKeySpec;

public class FFDHENegotiator extends AbstractNegotiator {

    private final FFDHEParemeters parameters;
    private final KeyAgreement keyAgreement;
    private final byte[] publicPart;

    private FFDHENegotiator(NamedGroup  group, KeyAgreement keyAgreement, 
            FFDHEParemeters parameters, byte[] publicPart, StructFactory factory) {

        super(group, factory);
        this.parameters = parameters;
        this.keyAgreement = keyAgreement;
        this.publicPart = publicPart;
    }

    public static FFDHENegotiator create(NamedGroup.FFDHE group, StructFactory factory) 
            throws NegotiatorException {

        try {
            FFDHEParemeters parameters = FFDHEParemeters.create(group);

            KeyPairGenerator generator = KeyPairGenerator.getInstance("DiffieHellman");
            generator.initialize(parameters.spec);
            KeyPair kp = generator.generateKeyPair();

            KeyAgreement keyAgreement = KeyAgreement.getInstance("DiffieHellman");
            keyAgreement.init(kp.getPrivate(), parameters.spec);

            BigInteger y = ((DHPublicKey) kp.getPublic()).getY();
            if (y.compareTo(BigInteger.ONE) <= 0 || y.compareTo(parameters.p) >= 0) {
                throw new RuntimeException();
            }

            byte[] publicPart = Convertor.leftPadding(
                    y.toByteArray(), parameters.p.toByteArray().length);

            return new FFDHENegotiator(
                    group, keyAgreement, parameters, publicPart, factory);

        } catch (InvalidKeyException | NoSuchAlgorithmException
                | InvalidAlgorithmParameterException e) {
            throw new NegotiatorException(e);
        }
    }

    @Override
    public KeyShareEntry createKeyShareEntry() {
        return factory.createKeyShareEntry(group, publicPart);
    }

    @Override
    public void processKeyShareEntry(KeyShareEntry  entry) throws NegotiatorException {
        if (!group.equals(entry.getNamedGroup())) {
            throw new IllegalArgumentException();
        }

        try {
            PublicKey key = KeyFactory.getInstance("DiffieHellman").generatePublic(
                    new DHPublicKeySpec(
                            new BigInteger(1, entry.getKeyExchange().encoding()),
                            parameters.p,
                            parameters.g));

            keyAgreement.doPhase(key, true);
        } catch (NoSuchAlgorithmException | IOException | InvalidKeyException
                | InvalidKeySpecException e) {
            throw new NegotiatorException(e);
        }
    }

    @Override
    public byte[] generateSecret() {
        return keyAgreement.generateSecret();
    }

}
