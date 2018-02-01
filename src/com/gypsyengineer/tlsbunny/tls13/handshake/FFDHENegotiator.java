package com.gypsyengineer.tlsbunny.tls13.handshake;

import com.gypsyengineer.tlsbunny.tls13.struct.impl.KeyShareEntryImpl;
import com.gypsyengineer.tlsbunny.tls13.struct.impl.NamedGroupImpl;
import com.gypsyengineer.tlsbunny.utils.Convertor;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHPublicKeySpec;

public class FFDHENegotiator extends AbstractNegotiator {

    private final FFDHEParemeters parameters;
    private final KeyAgreement keyAgreement;
    private final byte[] publicPart;

    private FFDHENegotiator(NamedGroupImpl group, KeyAgreement keyAgreement, 
            FFDHEParemeters parameters, byte[] publicPart) {

        super(group);
        this.parameters = parameters;
        this.keyAgreement = keyAgreement;
        this.publicPart = publicPart;
    }

    public static FFDHENegotiator create(NamedGroupImpl.FFDHEImpl group) throws Exception {
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
        
        return new FFDHENegotiator(group, keyAgreement, parameters, publicPart);
    }

    @Override
    public KeyShareEntryImpl createKeyShareEntry() {
        return KeyShareEntryImpl.wrap(group, publicPart);
    }

    @Override
    public void processKeyShareEntry(KeyShareEntryImpl entry) throws Exception {
        if (!group.equals(entry.getNamedGroup())) {
            throw new IllegalArgumentException();
        }

        PublicKey key = KeyFactory.getInstance("DiffieHellman").generatePublic(
                new DHPublicKeySpec(
                        new BigInteger(1, entry.getKeyExchange().encoding()),
                        parameters.p,
                        parameters.g));
        
        keyAgreement.doPhase(key, true);
    }

    @Override
    public byte[] generateSecret() {
        return keyAgreement.generateSecret();
    }

}
